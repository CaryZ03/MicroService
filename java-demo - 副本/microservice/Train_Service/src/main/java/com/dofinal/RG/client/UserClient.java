package com.dofinal.RG.client;

import com.dofinal.RG.entity.user.UserNotice;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Classname UserClient
 * Description TODO
 * Date 2024/8/24 10:49
 * Created ZHW
 */
@FeignClient(value = "user-service", url = "${backend.user.url}")
public interface UserClient {
    @PostMapping("/notice/insert")
    Integer insertUserNotice(@RequestBody UserNotice userNotice);

    @PostMapping("/notice/update/nid/{nid}")
    Integer updateStatus(@PathVariable int nid);

    @PostMapping("/notice/delete/nid/{nid}")
    Integer deleteNotice(@PathVariable int nid);
}
