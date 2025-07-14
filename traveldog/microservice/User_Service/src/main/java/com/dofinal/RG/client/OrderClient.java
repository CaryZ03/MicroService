package com.dofinal.RG.client;

import com.dofinal.RG.entity.order.HotelCustomerOrder;
import com.dofinal.RG.entity.order.TrainCustomerOrder;
import com.dofinal.RG.entity.order.UserOrder;
import com.dofinal.RG.entity.order.UserOrderDemo;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Classname OrderClient
 * Description TODO
 * Date 2024/8/23 10:57
 * Created ZHW
 */
@FeignClient(value = "order-service",url="${backend.order.url}")
public interface OrderClient {
    @GetMapping("/order/getUserOrderDemoByUid/{uid}")
    List<UserOrderDemo> getUserOrdersByUid(@PathVariable String uid);

    @GetMapping("/order/findTrainOrderByTid/{tid}")
    List<TrainCustomerOrder> findTrainOrderByTid(@PathVariable String tid);

    @GetMapping("/order/findTrainOrderByOid/{oid}")
    List<TrainCustomerOrder> findTrainOrderByOid(@PathVariable Integer oid);

    @GetMapping("/order/getUserOrderByOid/{oid}")
    UserOrder getUserOrderByOid(@PathVariable Integer oid);

    @GetMapping("/order/findHotelOrderByHid/{hid}")
    List<HotelCustomerOrder> findHotelOrderByHid(@PathVariable Integer hid);

    @GetMapping("/order/findHotelOrderByOid/{oid}")
    List<HotelCustomerOrder> findHotelOrderByOid(@PathVariable Integer oid);

    @GetMapping("/order/getOidByUid/{uid}")
    List<Integer> getOidByUid(@PathVariable String uid);

    @GetMapping("/order/deleteHotelOderByOid/{oid}")
    Integer deleteHotelOderByOid(@PathVariable Integer oid);

    @GetMapping("/order/deleteMealOrderByOid/{oid}")
    Integer deleteMealOrderByOid(@PathVariable Integer oid);

    @GetMapping("/order/deleteTrainOrderByOid/{oid}")
    Integer deleteTrainOrderByOid(@PathVariable Integer oid);

    @GetMapping("/order/deleteUserOrderByUid/{uid}")
    Integer deleteUserOrderByUid(@PathVariable String uid);
}
