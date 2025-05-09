package com.dofinal.RG;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.dofinal.RG.mapper")
@EnableConfigServer
@EnableFeignClients(basePackages = "com.dofinal.RG.client")
@ComponentScan({"com.dofinal.RG.client",
        "com.dofinal.RG.controller",
        "com.dofinal.RG.service",
        "com.dofinal.RG.mapper",
        "com.dofinal.RG.util"
})
@EnableDiscoveryClient
public class TrainApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainApplication.class, args);
    }

}
