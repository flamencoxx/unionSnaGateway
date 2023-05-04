package com.iaspec.uniongatewaymock;

import com.iaspec.uniongatewaymock.constant.GatewayConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ImportResource({GatewayConstant.FILE_APPLICATION_CONTEXT_PATH_1, GatewayConstant.FILE_APPLICATION_CONTEXT_PATH_2})
@EnableScheduling
public class UnionGatewayMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnionGatewayMockApplication.class, args);
    }

}
