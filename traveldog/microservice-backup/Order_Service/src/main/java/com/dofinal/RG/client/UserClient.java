package com.dofinal.RG.client;

import com.dofinal.RG.entity.user.Customer;
import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.entity.user.UserNotice;
import com.dofinal.RG.rsps.Notice.NoticeRsp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "user-service", url = "${backend.user.url}")
public interface UserClient {
    @GetMapping("/customer/getByCidMicro/{cid}")
    Customer getCustomerByCid(@PathVariable int cid);

    @GetMapping("/user/getByUidMicro/{uid}")
    User getUserByUid(@PathVariable String uid);

    @PostMapping("/user/updateUser/")
    Integer updateUser(@RequestBody  User user);

    @PostMapping("/notice/insert")
    Integer insertUserNotice(@RequestBody UserNotice userNotice);

    @PostMapping("/notice/update/nid/{nid}")
    Integer updateStatus(@PathVariable int nid);

    @PostMapping("/notice/delete/nid/{nid}")
    Integer deleteNotice(@PathVariable int nid);

    @PostMapping("/notice/handle/")
    void handleOrderNotice(@RequestBody NoticeRsp rsp, @RequestParam("uid") String uid);
}
