package com.iaspec.uniongatewayserver;

import com.iaspec.uniongatewayserver.constant.GatewayConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ImportResource({GatewayConstant.FILE_APPLICATION_CONTEXT_PATH_1,
        GatewayConstant.FILE_APPLICATION_CONTEXT_PATH_2,
        GatewayConstant.FILE_APPLICATION_CONTEXT_PATH_3,
        GatewayConstant.FILE_APPLICATION_CONTEXT_PATH_4})
public class UnionGatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnionGatewayServerApplication.class, args);
    }

}
