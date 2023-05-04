package com.iaspec.uniongatewaymock.model;

import lombok.Data;

/**
 * @author Flamenco.xxx
 * @date 2022/9/22  10:50
 */

@Data
public class TimerResultDTO {

    private String sendTimes;

    private String successTimes;

    private String failTimes;

    private String count0And5;

    private String count5And15;

    private String count15And30;

    private String count30And60;

    private String count60And100;

    private String count100And9999;


    private String count0And100;

    private String count100And200;

    private String count100And300;

    private String count0And300;

    private String count800And9999;

    private String count400And500;

    private String count500And600;

    private String count600And700;

    private String count700And800;

    private String count200And300;

    private String count300And400;

    private String count400And1000;

    private String count1000And5000;

    private String count5000;

    private String avgTimer;

    private String countLessThan0;

    private String countAllSend;
}
