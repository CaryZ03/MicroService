package com.ejt.demo.server;

import com.ejt.demo.server.controls.SimulatorControl;
import com.ejt.demo.server.handlers.*;
import com.ejt.demo.server.mbean.StandardTest;
import com.ejt.demo.server.mbean.Test;
import com.ejt.mock.MockCallable;
import com.ejt.mock.jms.MockMessage;
import com.ejt.mock.servlet.MockHttpServletRequest;
import com.ejt.mock.servlet.MockServlet;
import com.ejt.mock.servlet.MockSession;
import org.uncommons.maths.random.ExponentialGenerator;

import javax.management.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public abstract class DemoServer<SC extends SimulatorControl> implements ServerControl<SC> {

    public static final int MILLISECONDS_TO_MINUTE = 60000;
    public static final int MAX_REQUEST_SIMULATORS = 5;
    public static final int MAX_SESSIONS = 10;
    public static final String VIEW_NAME = "demo/view";
    public static final String PROPERTY_DEMO_PERFINO = "demo.perfino";

    private static final List<String> USERS = Arrays.asList("John", "Jane", "Max", "Bob", "Barbara", "Tricia", "Fred", "Greg", "Ella", "Dorothy");
    private static final List<String> IPS = Arrays.asList("192.168.3.33", "192.168.45.2", "192.168.1.29", "192.168.24.175", "192.168.33.152");

    private EnumSet<SimulatorType> simulatorTypes;
    private Random random = new Random(System.nanoTime());

    private List<MockSession> sessions;
    private SC requestSimulatorControl;
    private SC jdbcJobSimulatorControl;
    private SC jmsSimulatorControl;
    private RmiHandlerImpl rmiHandler;

    public DemoServer(EnumSet<SimulatorType> simulatorTypes) {
        this.simulatorTypes = simulatorTypes;

        initSessions();
        requestSimulatorControl = createRequestSimulatorControl();
        jdbcJobSimulatorControl = createJdbcJobSimulatorControl();
        jmsSimulatorControl = createJmsSimulatorControl();

    }

    protected abstract SC createJmsSimulatorControl();
    protected abstract SC createJdbcJobSimulatorControl();
    protected abstract SC createRequestSimulatorControl();

    @Override
    public SC getRequestSimulatorControl() {
        return requestSimulatorControl;
    }

    @Override
    public SC getJdbcJobSimulatorControl() {
        return jdbcJobSimulatorControl;
    }

    @Override
    public SC getJmsSimulatorControl() {
        return jmsSimulatorControl;
    }

    public void start() throws Exception {

        registerTestMBeans();

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, DemoInitialContextFactory.class.getName());
        final Context context = new InitialContext(env);
        startSimulators(context);

        synchronized (this) {
            wait();
        }
    }

    private void initSessions() {
        sessions = new ArrayList<>();
        for (int i = 0; i < MAX_SESSIONS; i++) {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("user", USERS.get(i));
            sessions.add(new MockSession(String.valueOf(i + 1), attributes, IPS.get(i % IPS.size())));
        }
    }

    private void registerTestMBeans() {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        try {
            ObjectName mxName = new ObjectName("com.ejt.demo:type=Test");
            server.registerMBean(new Test(), mxName);
            ObjectName standardName = new ObjectName("com.ejt.demo:type=StandardTest");
            server.registerMBean(new StandardTest(), standardName);
        } catch (MalformedObjectNameException | NotCompliantMBeanException | InstanceAlreadyExistsException | MBeanRegistrationException e) {
            e.printStackTrace();
        }
    }

    protected void startSimulators(final Context context) throws RemoteException {
        if (simulatorTypes.contains(SimulatorType.HTTP_CALLS)) {
            DemoHttpServer.start();
        }
        if (simulatorTypes.contains(SimulatorType.RMI)) {
            startRmiServer(context);
        }

        for (int i = 0; i < MAX_REQUEST_SIMULATORS; i++) {
            startSimulator(SimulatorType.REQUEST, i + 1, MAX_REQUEST_SIMULATORS,
                    requestSimulatorControl,
                    new MockCallable() {
                        @Override
                        public void run() throws Exception {
                            simulateRequest(context);
                        }
                    }
            );
        }

        startSimulator(SimulatorType.JDBC_JOB,
                jdbcJobSimulatorControl,
                createJdbcJobHandler(context)
        );

        startSimulator(SimulatorType.JMS,
                jmsSimulatorControl,
                new MockCallable() {
                    @Override
                    public void run() throws Exception {
                        simulateJmsMessage();
                    }
                }
        );
    }

    protected JdbcJobHandler createJdbcJobHandler(Context context) {
        return new JdbcJobHandler(context);
    }

    protected RequestHandler createRequestHandler(int viewNumber, Context context, int rmiRegistryPort) {
        return new RequestHandler(viewNumber, context, rmiRegistryPort);
    }

    protected JmsHandler createJmsHandler(int rmiRegistryPort) {
        return new JmsHandler(rmiRegistryPort);
    }

    protected RmiHandlerImpl createRmiHandlerImpl(Context context) {
        return new RmiHandlerImpl(context);
    }

    protected void startRmiServer(Context context) throws RemoteException {
        rmiHandler = createRmiHandlerImpl(context);
        RmiHandler stub = (RmiHandler)UnicastRemoteObject.exportObject(rmiHandler, 0);
        if (Boolean.getBoolean("perfino.logRmi")) {
            UnicastRemoteObject.setLog(System.err);
        }
        Registry registry = LocateRegistry.createRegistry(getRmiRegistryPort());
        registry.rebind(RmiHandler.NAME, stub);
    }

    protected int getRmiRegistryPort() {
        return RmiHandler.PORT;
    }

    private void simulateRequest(Context context) throws Exception {
        int viewNumber = random.nextInt(5) + 1;
        String queryString = "action=list&random=" + random.nextInt(1000);
        MockHttpServletRequest servletRequest = new MockHttpServletRequest("/" + VIEW_NAME + viewNumber, queryString, sessions.get(random.nextInt(10)));
        MockServlet servlet = new MockServlet(createRequestHandler(viewNumber, context, getRmiRegistryPort()));
        servlet.service(servletRequest, null);
    }

    private void simulateJmsMessage() {
        JmsHandler.JmsType jmsType = JmsHandler.JmsType.values()[random.nextInt(JmsHandler.JmsType.values().length)];
        int duration = jmsType.getDuration();
        createJmsHandler(getRmiRegistryPort()).onMessage(new MockMessage(jmsType.getDestination(), duration / 2 + random.nextInt(duration / 2)));
    }

    protected void startSimulator(SimulatorType simulatorType, SimulatorControl simulatorControl, MockCallable mockCallable) {
        startSimulator(simulatorType, 0, 1, simulatorControl, mockCallable);
    }

    protected void startSimulator(final SimulatorType simulatorType, final int number, final int simulatorCount, final SimulatorControl simulatorControl, final MockCallable mockCallable) {
        if (!simulatorTypes.contains(simulatorType)) {
            return;
        }

        new Thread() {
            {
                setName(simulatorType.toString() + (number > 0 ? " " + number : ""));
            }

            @Override
            public void run() {
                Random random = new Random(System.nanoTime());
                ExponentialGenerator gen = new ExponentialGenerator(simulatorControl, random);
                //noinspection InfiniteLoopStatement
                while (true) {
                    simulatorControl.waitForEnabled();

                    if (simulatorControl.isExecuteImmediately()) {
                        try {
                            mockCallable.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        long interval = Math.round(gen.nextValue() * simulatorCount * (long)MILLISECONDS_TO_MINUTE);
                        try {
                            Thread.sleep(interval);
                            mockCallable.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

}
