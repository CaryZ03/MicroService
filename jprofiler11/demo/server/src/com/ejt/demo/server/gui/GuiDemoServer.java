package com.ejt.demo.server.gui;

import com.ejt.demo.server.DemoServer;
import com.ejt.demo.server.SimulatorType;
import com.ejt.demo.server.controls.AdjustableSimulatorControl;

import java.awt.*;
import java.util.EnumSet;

public class GuiDemoServer extends DemoServer<AdjustableSimulatorControl> {

    public GuiDemoServer(EnumSet<SimulatorType> simulatorTypes) {
        super(simulatorTypes);
    }

    @Override
    protected AdjustableSimulatorControl createRequestSimulatorControl() {
        return new AdjustableSimulatorControl(Integer.getInteger("demo.requestRate", 50));
    }

    @Override
    protected AdjustableSimulatorControl createJdbcJobSimulatorControl() {
        return new AdjustableSimulatorControl(Integer.getInteger("demo.jdbcRate", 10));
    }

    @Override
    protected AdjustableSimulatorControl createJmsSimulatorControl() {
        return new AdjustableSimulatorControl(Integer.getInteger("demo.jmsRate", 10));
    }

    @Override
    public void stopServer() {
        System.exit(0);
    }

    public static void main(final String[] args) {
        System.setProperty("jdk.gtk.version", "2");
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final GuiDemoServer demoServer = new GuiDemoServer(getSimulatorTypes(args));
                new ServerControlFrame(demoServer).setVisible(true);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            demoServer.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    private static EnumSet<SimulatorType> getSimulatorTypes(String[] args) {
        if (args.length == 1 && args[0].equals("noremote")) {
            return EnumSet.complementOf(EnumSet.of(SimulatorType.RMI, SimulatorType.HTTP_CALLS));
        } else {
            return EnumSet.allOf(SimulatorType.class);
        }
    }
}
