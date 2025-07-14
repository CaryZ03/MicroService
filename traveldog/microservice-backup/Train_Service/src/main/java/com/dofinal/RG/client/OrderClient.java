package com.dofinal.RG.client;

import com.dofinal.RG.entity.order.TrainCustomerOrder;
import com.dofinal.RG.entity.order.UserOrder;
import com.dofinal.RG.entity.order.UserOrderDemo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Classname OrderClient
 * Description TODO
 * Date 2024/8/23 10:09
 * Created ZHW
 */
@FeignClient(value = "order-service",url = "${backend.order.url}")
public interface OrderClient {
    @GetMapping("/order/getUserOrderByUid/{uid}")
    List<UserOrder> getUserOrderByUid(@PathVariable String uid);

    @GetMapping("/order/getUserOrderByOid/{oid}")
    UserOrder getUserOrderByOid(@PathVariable Integer oid);

    @GetMapping("/order/getOidByTid/{tid}")
    List<Integer> getOrderIdsByTid(@PathVariable String tid);

    @GetMapping("/order/findTrainOrderByOid/{oid}")
    List<TrainCustomerOrder> findTrainOrderByOid(@PathVariable Integer oid);
}
