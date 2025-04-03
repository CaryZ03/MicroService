package com.dofinal.RG.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dofinal.RG.entity.hotel.HotelComment;
import com.dofinal.RG.entity.hotel.Hotel;
import com.dofinal.RG.entity.hotel.HotelRoom;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.order.*;
import com.dofinal.RG.entity.train.Meal;
import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.exceptions.PurchaseException;
import com.dofinal.RG.mapper.*;
import com.dofinal.RG.reqs.entity.HotelCommentReq;
import com.dofinal.RG.reqs.userOrder.*;
import com.dofinal.RG.rsps.Notice.NoticeRsp;
import com.dofinal.RG.service.MealService;
import com.dofinal.RG.service.OrderService;
import com.dofinal.RG.service.RoomService;
import com.dofinal.RG.service.SeatService;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * &#064;Classname OrderServiceImpl
 * &#064;Description TODO
 * &#064;Date 2024/6/2 17:48
 * &#064;Created MuJue
 */
@Service
public class OrderServiceImpl extends ServiceImpl<UserOrderMapper, UserOrder> implements OrderService {
    @Autowired
    private UserOrderMapper userOrderMapper;
    @Autowired
    private HotelRoomMapper hotelRoomMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private TrainMapper trainMapper;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private HotelOrderMapper hotelOrderMapper;
    @Autowired
    private TrainArrivalMapper trainArrivalMapper;
    @Autowired
    private SeatPriceMapper seatPriceMapper;
    @Autowired
    private TrainOrderMapper trainOrderMapper;
    @Autowired
    private TrainMealMapper trainMealMapper;
    @Autowired
    private MealMapper mealMapper;
    @Autowired
    private MealOrderMapper mealOrderMapper;
    @Autowired
    HotelCommentMapper hotelCommentMapper;
    @Autowired
    private RoomService roomService;
    @Autowired
    private SeatService seatService;
    @Autowired
    private MealService mealService;

    @Trace
    public List<UserOrderDemo> getUserOrdersByUid(String uid) {
        Timestamp curTime = new Timestamp(System.currentTimeMillis() / 1000 * 1000);

        if (uid == null)
            return null;
        List<UserOrder> userOrderList = userOrderMapper.getUserOrderByUid(uid);
        if (userOrderList == null || userOrderList.isEmpty()) {
            return null;
        }
        Collections.sort(userOrderList);
        List<UserOrderDemo> userOrderDemoList = new ArrayList<>();
        for (UserOrder userOrder : userOrderList) {
            int oid = userOrder.getOrderId();

            UserOrderDemo userOrderDemo = new UserOrderDemo(oid, userOrder.getUserId(),
                    userOrder.getType(), userOrder.getAddTime(), userOrder.getStatus(),
                    userOrder.getPrice(), userOrder.isComment());

            String cname;
            String tid;
            String hname;

            switch (userOrder.getType()) {
                case "hotelOrder":
                    List<HotelCustomerOrder> hcos = hotelOrderMapper.findHotelOrderByOid(oid);
                    List<HotelCustomerOrderDemo> hcods = new ArrayList<>();

                    HotelCustomerOrder hco = hcos.get(0);
                    int hid = hco.getHotelId();
                    hname = hotelMapper.getHotelByHid(hid).getName();
                    userOrderDemo.setHname(hname);

//                    if (hco.getFromTime().before(curTime) && status.equals("not paid")) {
//                        cancelUserOrder(new OtherUserOrderReq(uid, oid));
//                    }
//                    if (hco.getToTime().before(curTime) && status.equals("paid")) {
//                        userOrderMapper.updateUserOrderStatusByOid(oid, "finished");
//                        userOrderDemo.setStatus("finished");
//                    }

                    for (HotelCustomerOrder tmpHco : hcos) {
                        int cid = tmpHco.getCustomerId();
                        cname = customerMapper.findCustomerByCid(cid).getName();

                        HotelCustomerOrderDemo hcod = new HotelCustomerOrderDemo(cid, cname, tmpHco.getPrice(), hname,
                                tmpHco.getRoomNumber(), tmpHco.getFromTime(), tmpHco.getToTime());
                        hcods.add(hcod);
                    }
                    userOrderDemo.setHotelCustomerOrderDemos(hcods);
                    break;

                case "trainOrder":
                    List<TrainCustomerOrder> tcos = trainOrderMapper.findTrainOrderByOid(oid);
                    List<TrainCustomerOrderDemo> tcods = new ArrayList<>();

                    TrainCustomerOrder tco = tcos.get(0);
                    tid = tco.getTrainId();

                    Location departureLocation = locationMapper.getLocationByLid(tco.getDepartLocationId());
                    Location arriveLocation = locationMapper.getLocationByLid(tco.getArriveLocationId());

                    userOrderDemo.setTid(tid);

//                    if (tco.getArriveTime().before(curTime) && status.equals("not paid")) {
//                        cancelUserOrder(new OtherUserOrderReq(uid, oid));
//                        List<MealCustomerOrder> mealOrders = mealOrderMapper.getMealOrderByTid(tid);
//                        Set<Integer> oids = new HashSet<>();
//                        for (MealCustomerOrder mealCustomerOrder : mealOrders) {
//                            oids.add(mealCustomerOrder.getOrderId());
//                        }
//                        for (Integer tmpOid : oids) {
//                            cancelUserOrder(new OtherUserOrderReq(uid, tmpOid));
//                        }
//                    }
//                    if (tco.getArriveTime().before(curTime) && status.equals("paid")) {
//                        userOrderMapper.updateUserOrderStatusByOid(oid, "finished");
//                        userOrderDemo.setStatus("finished");
//                        List<MealCustomerOrder> mealOrders = mealOrderMapper.getMealOrderByTid(tid);
//                        Set<Integer> oids = new HashSet<>();
//                        for (MealCustomerOrder mealCustomerOrder : mealOrders) {
//                            oids.add(mealCustomerOrder.getOrderId());
//                        }
//                        for (Integer tmpOid : oids) {
//                            userOrderMapper.updateUserOrderStatusByOid(tmpOid, "finished");
//                        }
//                    }

                    for (TrainCustomerOrder tmpTco : tcos) {
                        cname = customerMapper.findCustomerByCid(tmpTco.getCustomerId()).getName();
                        TrainCustomerOrderDemo tcod = new TrainCustomerOrderDemo(tmpTco.getCustomerId(), cname,
                                tmpTco.getPrice(), tmpTco.getTrainId(),
                                tmpTco.getSeatNumber(), tmpTco.getSeatType(), departureLocation, arriveLocation,
                                tmpTco.getDepartureTime(),
                                tmpTco.getArriveTime());
                        tcods.add(tcod);
                    }
                    userOrderDemo.setTrainCustomerOrderDemos(tcods);
                    break;
                case "mealOrder":
                    List<MealCustomerOrder> mcos = mealOrderMapper.getMealOrderByOid(oid);
                    MealCustomerOrder tmpMco = mcos.get(0);
                    List<MealCustomerOrderDemo> mcods = new ArrayList<>();

                    tid = tmpMco.getTrainId();

                    for (MealCustomerOrder mco : mcos) {
                        cname = customerMapper.findCustomerByCid(mco.getCustomerId()).getName();
                        String mname = mealMapper.getMealByMid(mco.getMealId()).getName();
                        MealCustomerOrderDemo mcod = new MealCustomerOrderDemo(tid, mco.getCustomerId(), cname,
                                mco.getPrice(), mname);
                        mcods.add(mcod);
                    }
                    userOrderDemo.setTid(tid);
                    userOrderDemo.setMealCustomerOrderDemos(mcods);
                    break;
            }
            userOrderDemoList.add(userOrderDemo);
        }
        return userOrderDemoList;
    }

    private void setUserOrderPrice(UserOrder userOrder, String type) {
        if (type.equals("hotelOrder")) {
            List<HotelCustomerOrder> orders = hotelOrderMapper.findHotelOrderByOid(userOrder.getOrderId());
            for (HotelCustomerOrder order : orders) {
                userOrder.setPrice(userOrder.getPrice() + order.getPrice());
            }
        } else if (type.equals("trainOrder")) {
            List<TrainCustomerOrder> orders = trainOrderMapper.findTrainOrderByOid(userOrder.getOrderId());
            for (TrainCustomerOrder order : orders) {
                userOrder.setPrice(userOrder.getPrice() + order.getPrice());
            }
        } else if (type.equals("mealOrder")) {
            List<MealCustomerOrder> orders = mealOrderMapper.getMealOrderByOid(userOrder.getOrderId());
            for (MealCustomerOrder order : orders) {
                userOrder.setPrice(userOrder.getPrice() + order.getPrice());
            }
        }
        userOrderMapper.updateUserOrderPriceByOid(userOrder.getOrderId(), userOrder.getPrice());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Trace
    public NoticeRsp generateUserOrder(AddUserOrderReq req) {
        NoticeRsp rsp = new NoticeRsp();
        rsp.setOperationType("generate");

        Timestamp curTime = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String uid = req.getUid();
        String type = req.getOrderType();

        List<Integer> cids = new ArrayList<>();
        List<String> customerNames = new ArrayList<>();

        UserOrder userOrder = new UserOrder();
        userOrder.setUserId(uid);
        userOrder.setPrice(0.0);
        userOrder.setAddTime(curTime);
        userOrder.setStatus("not paid");
        userOrder.setType(null);
        userOrder.setComment(false);
        userOrderMapper.insertUserOrder(userOrder);

        int oid = userOrder.getOrderId();
        rsp.setOid(oid);
        rsp.setOrderType(type);

        boolean success = true;

        if (type.equals("hotelOrder")) {

            HotelOrderReq ho = req.getHor();

            cids = req.getHor().getCids();

            rsp.setMainName(hotelMapper.getHotelByHid(ho.getHid()).getName());

            if (generateHotelOrder(oid, ho, curTime) > 0) {
                setUserOrderPrice(userOrder, "hotelOrder");
            } else {
                userOrderMapper.deleteUserOrderByOid(oid);
                success = false;
            }
        } else if (type.equals("trainOrder")) {
            TrainOrderReq to = req.getTor();

            cids = req.getTor().getCids();

            rsp.setMainName(to.getTid());

            if (generateTrainOrder(oid, to, curTime) > 0) {
                setUserOrderPrice(userOrder, "trainOrder");
            } else {
                userOrderMapper.deleteUserOrderByOid(oid);
                success = false;
            }
        } else if (type.equals("mealOrder")) {
            MealOrderReq mo = req.getMor();

            cids = req.getMor().getCids();

            rsp.setMainName(mo.getTid() + "火车的" + mealMapper.getMealByMid(mo.getMid()).getName());

            if (generateMealOrder(oid, mo, curTime) > 0) {
                setUserOrderPrice(userOrder, "mealOrder");
            } else {
                userOrderMapper.deleteUserOrderByOid(oid);
                success = false;
            }
        }
        for (Integer cid : cids) {
            String customerName = customerMapper.findCustomerByCid(cid).getName();
            customerNames.add(customerName);
        }

        rsp.setPrice(userOrder.getPrice());
        rsp.setCustomerName(customerNames);

        if (success) {
            rsp.setRet(1);
        } else {
            rsp.setRet(0);
        }
        return rsp;
    }

    @Trace
    public int generateHotelOrder(int oid, HotelOrderReq req, Timestamp curTime) {
        if (req == null)
            return 0;

        userOrderMapper.updateUserOrderTypeByOid(oid, "hotelOrder");

        List<Integer> cids = req.getCids();
        int length = cids.size();
        List<HotelCustomerOrder> tempHotelCustomerOrders = req.getInstance();

        Timestamp fromTime = req.getFromTime();
        Timestamp toTime = req.getToTime();

        int hid = req.getHid();
        int rid = req.getRid();

        List<HotelRoom> hotelRooms = roomService.findHotelRoomByHidRidTime(hid, rid, curTime, fromTime, toTime);

        if (hotelRooms.size() < length) {
            return 0;
        } else {
            double price_rate = hotelMapper.findHotelPriceRateByHid(hid);
            for (int i = 0; i < length; ++i) {
                // 获得HotelRoom
                HotelRoom hr = hotelRooms.get(i);
                double price = roomMapper.getRoomPriceByRid(rid) * price_rate;

                // 订购房间（数据库逻辑）
                byte[] bitmap = hr.getBitmap();
                bitmap = roomService.orderRoom(curTime, fromTime, toTime, bitmap);
                hr.setBitmap(bitmap);
                hotelRoomMapper.updateHotelRoom(hr);

                // 插入顾客订单
                HotelCustomerOrder hco = tempHotelCustomerOrders.get(i);
                hco.setOrderId(oid);
                hco.setAddTime(curTime);
                hco.setRoomNumber(hr.getRoomNumber());
                hco.setPrice(price);
                hotelOrderMapper.insert(hco);
            }
        }
        // 更新用户订单
        return 1;
    }

    @Trace
    public int generateTrainOrder(int oid, TrainOrderReq req, Timestamp curTime) {
        if (req == null)
            return 0;

        userOrderMapper.updateUserOrderTypeByOid(oid, "trainOrder");

        List<Integer> cids = req.getCids();
        int length = cids.size();
        List<TrainCustomerOrder> orders = req.getInstance();

        String tid = req.getTid();
        String type = req.getSeatType();
        Location bl = req.getBeginLocation();
        Location el = req.getEndLocation();

        int dlid = locationMapper.getLidByProvinceCity(bl.getProvince(), bl.getCity());
        int alid = locationMapper.getLidByProvinceCity(el.getProvince(), el.getCity());
        int bs = trainArrivalMapper.getStationSequenceByTidAndLid(tid, dlid);
        int as = trainArrivalMapper.getStationSequenceByTidAndLid(tid, alid);
        Timestamp depature_time = trainArrivalMapper.getTrainArrivalTimeByTidLid(tid, dlid);
        Timestamp arrive_time = trainArrivalMapper.getTrainArrivalTimeByTidLid(tid, alid);

        int count = seatService.getSeatCountByTidAndStationIndex(tid, bs, as, type);
        if (count < length) {
            return 0;
        } else {
            double price_rate = trainMapper.findTrainPriceRateByTid(tid);
            for (int i = 0; i < length; ++i) {
                // 进行座位的购买（数据库的逻辑）
                String sid = seatService.buySeatByTidAndStationIndex(tid, bs, as, type);
                double price = seatPriceMapper.getPriceBySeatType(type) * price_rate;
                // 生成订单
                TrainCustomerOrder tco = orders.get(i);
                tco.setAddTime(curTime);
                tco.setDepartureTime(depature_time);
                tco.setArriveTime(arrive_time);
                tco.setDepartLocationId(dlid);
                tco.setArriveLocationId(alid);
                tco.setOrderId(oid);
                tco.setSeatNumber(sid);
                tco.setPrice(price);
                trainOrderMapper.insert(tco);
            }
        }
        return 1;
    }

    @Trace
    public int generateMealOrder(int oid, MealOrderReq req, Timestamp curTime) {

        userOrderMapper.updateUserOrderTypeByOid(oid, "mealOrder");

        String tid = req.getTid();
        int mid = req.getMid();

        List<Integer> cids = req.getCids();
        int length = cids.size();
        List<MealCustomerOrder> orders = req.getInstance();

        int count = trainMealMapper.getTrainMealCountByTidAndMid(tid, mid);

        if (count < length) {
            return 0;
        } else {
            Meal meal = mealMapper.getMealByMid(mid);
            double price_rate = trainMapper.findTrainPriceRateByTid(tid);
            for (int i = 0; i < length; ++i) {
                // 购买火车餐（数据库逻辑）
                mealService.buyMeal(tid, mid);
                MealCustomerOrder mco = orders.get(i);
                double price = mealMapper.getMealPriceByMid(mid) * price_rate;

                mco.setAddTime(curTime);
                mco.setOrderId(oid);
                mco.setPrice(price);
                mealOrderMapper.insert(mco);
            }
        }
        return 1;
    }

    @Override
    @Trace
    public NoticeRsp cancelUserOrder(OtherUserOrderReq req) {
        NoticeRsp rsp = new NoticeRsp();
        rsp.setOperationType("cancel");

        String uid = req.getUid();
        int oid = req.getOid();
        UserOrder order = userOrderMapper.getUserOrderByOid(oid);
        String status = order.getStatus();
        String type = order.getType();

        Set<Integer> tmpCids = new HashSet<>();
        List<String> customerNames = new ArrayList<>();
        rsp.setOrderType(type);
        rsp.setOid(oid);

        if (type.equals("trainOrder")) {

            List<TrainCustomerOrder> orders = trainOrderMapper.findTrainOrderByOid(oid);
            Set<Integer> cids = new HashSet<>();
            for (TrainCustomerOrder order1 : orders) {
                cids.add(order1.getCustomerId());
                tmpCids.add(order1.getCustomerId());
            }
            String tid = orders.get(0).getTrainId();
            cancelTrainOrder(orders);
            rsp.setMainName(tid);

            List<MealCustomerOrder> mealCustomerOrders = new ArrayList<>();
            for (Integer cid : cids) {
                List<MealCustomerOrder> tmp = mealOrderMapper.getMealOrderByTidCid(tid, cid);
                for (MealCustomerOrder t : tmp) {
                    UserOrder userOrder = userOrderMapper.getUserOrderByOid(t.getOrderId());
                    String status1 = userOrder.getStatus();
                    if (status1.equals("paid") || status1.equals("not paid")) {
                        mealCustomerOrders.add(t);
                    }
                }
            }
            if (!mealCustomerOrders.isEmpty()) {
                // cancelMealOrder(mealCustomerOrders);
                for (MealCustomerOrder mealCustomerOrder : mealCustomerOrders) {
                    String status1 = userOrderMapper.getUserOrderByOid(mealCustomerOrder.getOrderId()).getStatus();
                    if (status1.equals("paid") || status1.equals("not paid")) {
                        cancelUserOrder(new OtherUserOrderReq(uid, mealCustomerOrder.getOrderId()));
                    }
                }
            }
            // for(MealCustomerOrder mealCustomerOrder : mealCustomerOrders){
            // oids.add(mealCustomerOrder.getOrderId());
            // }
            // for(Integer oid1 : oids){
            // UserOrder userOrder = userOrderMapper.getUserOrderByOid(oid1);
            // if(userOrder.getStatus().equals("paid")){
            // User user = userMapper.findUserByUid(uid);
            // user.setMoney(user.getMoney() + userOrder.getPrice());
            // userMapper.updateUser(user);
            // }
            // userOrderMapper.updateUserOrderStatusByOid(oid1, "canceled");
            // }

        } else if (type.equals("mealOrder")) {
            List<MealCustomerOrder> orders = mealOrderMapper.getMealOrderByOid(oid);
            rsp.setMainName(mealMapper.getMealByMid(orders.get(0).getMealId()).getName());
            for (MealCustomerOrder order1 : orders) {
                tmpCids.add(order1.getCustomerId());
            }
            cancelMealOrder(orders);
        } else if (type.equals("hotelOrder")) {
            List<HotelCustomerOrder> orders = hotelOrderMapper.findHotelOrderByOid(oid);
            hotelCommentMapper.deleteHotelRateByOid(oid);

            for (HotelCustomerOrder order1 : orders) {
                tmpCids.add(order1.getCustomerId());
            }
            cancelHotelOrder(orders);
        }
        if (status.equals("paid")) {
            User user = userMapper.findUserByUid(uid);
            user.setMoney(user.getMoney() + order.getPrice());
            userMapper.updateUser(user);
        }
        userOrderMapper.updateUserOrderStatusByOid(oid, "canceled");

        for (Integer cid : tmpCids) {
            String customerName = customerMapper.findCustomerByCid(cid).getName();
            customerNames.add(customerName);
        }
        rsp.setPrice(order.getPrice());
        rsp.setRet(1);
        rsp.setCustomerName(customerNames);
        return rsp;
    }

    @Override
    @Trace
    public int rateHotel(HotelCommentReq req) {
        UserOrder order = userOrderMapper.getUserOrderByOid(req.getOid());
        if (order == null || !order.getType().equals("hotelOrder"))
            return 0;

        int oid = order.getOrderId();
        List<HotelCustomerOrder> hcos = hotelOrderMapper.findHotelOrderByOid(oid);
        int hid = hcos.get(0).getHotelId();

        Hotel hotel = hotelMapper.getHotelByHid(hid);
        if (hotel == null)
            return 0;

        HotelComment hotelComment = new HotelComment(oid, hid, req.getRate(), req.getComment());
        HotelComment hotelComment1 = hotelCommentMapper.getHotelCommentByOid(oid);
        if(hotelComment1 != null) {
            hotelCommentMapper.deleteHotelRateByOid(oid);
        }
        hotelCommentMapper.insertHotelRate(hotelComment);
        order.setComment(true);
        userOrderMapper.updateUserOrderCommentStatus(oid, true);
        return 1;
    }

    @Override
    @Trace
    public HotelComment getHotelComment(HotelCommentReq req) {
        return  hotelCommentMapper.getHotelCommentByOid(req.getOid());

    }

    @Override
    @Trace
    public int deleteHotelComment(HotelCommentReq req) {
        int oid = req.getOid();
        hotelCommentMapper.deleteHotelRateByOid(oid);
        UserOrder userOrder = userOrderMapper.getUserOrderByOid(oid);
        userOrder.setComment(false);
        userOrderMapper.updateUserOrderCommentStatus(oid, false);
        return 1;
    }

    @Trace
    public NoticeRsp deleteUserOrder(OtherUserOrderReq req) {
        NoticeRsp rsp = new NoticeRsp();
        int oid = req.getOid();
        rsp.setRet(1);
        rsp.setOperationType("delete");
        rsp.setOid(oid);
        UserOrder userOrder = userOrderMapper.getUserOrderByOid(oid);
        String orderType = userOrder.getType();
        rsp.setOrderType(orderType);

        trainOrderMapper.deleteTrainOrderByOid(oid);
        hotelOrderMapper.deleteHotelOderByOid(oid);
        mealOrderMapper.deleteMealOrderByOid(oid);
        userOrderMapper.deleteUserOrderByOid(oid);

        return rsp;
    }

    @Trace
    public int cancelTrainOrder(List<TrainCustomerOrder> orders) {
        for (TrainCustomerOrder order : orders) {
            String tid = order.getTrainId();
            int dlid = order.getDepartLocationId();
            int alid = order.getArriveLocationId();
            int bs = trainArrivalMapper.getStationSequenceByTidAndLid(tid, dlid);
            int as = trainArrivalMapper.getStationSequenceByTidAndLid(tid, alid);

            String sid = order.getSeatNumber();
            seatService.cancelSeatByTidAndStationIndex(tid, sid, bs, as);
        }
        return 1;
    }

    @Trace
    public int cancelMealOrder(List<MealCustomerOrder> orders) {
        for (MealCustomerOrder order : orders) {
            mealService.cancelMeal(order.getTrainId(), order.getMealId());
        }
        return 1;
    }

    @Trace
    public int cancelHotelOrder(List<HotelCustomerOrder> orders) {
        for (HotelCustomerOrder order : orders) {
            Timestamp curTime = order.getAddTime();
            Timestamp startTime = order.getFromTime();
            Timestamp endTime = order.getToTime();
            int hid = order.getHotelId();
            int rid = order.getRoomId();
            String rNumber = order.getRoomNumber();
            HotelRoom hotelRoom = roomService.findHotelRoomByHidRidRNumber(hid, rid, rNumber);

            byte[] curBinary = hotelRoom.getBitmap();
            curBinary = roomService.cancelRoom(curTime, startTime, endTime, curBinary);
            hotelRoom.setBitmap(curBinary);
            hotelRoomMapper.updateHotelRoom(hotelRoom);
        }
        return 1;
    }

    @Override
    @Trace
    public NoticeRsp payUserOrder(OtherUserOrderReq req) throws PurchaseException {
        NoticeRsp rsp = new NoticeRsp();
        rsp.setOperationType("pay");

        String uid = req.getUid();
        UserOrder userOrder = userOrderMapper.getUserOrderByOid(req.getOid());

        String type = userOrder.getType();
        rsp.setOrderType(type);

        double price = userOrder.getPrice();
        double money;
        rsp.setOid(req.getOid());

        if (userOrder.getStatus().equals("not paid")) {
            User user = userMapper.findUserByUid(uid);

            money = user.getMoney();
            if (money < price) {
                rsp.setRet(-1);
                return rsp;
            }

            user.setMoney(money - price);
            userMapper.updateUser(user);
            userOrderMapper.updateUserOrderStatusByOid(req.getOid(), "paid");

            rsp.setPrice(price);
        }
        rsp.setRet(1);
        return rsp;
    }

}
