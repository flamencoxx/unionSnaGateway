package com.iaspec.uniongatewayserver.model;

import COM.ibm.eNetwork.cpic.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Flamenco.xxx
 * @date 2022/8/17  9:41
 */
//
//        conversation_ID,     /*  I: conversation ID            */
//                data_buffer,         /*  I: where to put received data */
//                requested_length,    /*  I: maximum length to receive  */
//                data_received,       /*  O: data complete or not?      */
//                received_length,     /*  O: length of received data    */
//                status_received,     /*  O: has status changed?        */
//                rts_received,        /*  O: was RTS received?          */
//                cpic_return_code);   /*  O: return code from this call */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcceptResult {

    private String conversationId;

    private byte[] data;

    private CPICLength requestLength;

    private CPICDataReceivedType dataReceivedType;

    private CPICLength receivedLength;

    private CPICStatusReceived statusReceived;

    private CPICControlInformationReceived rtsReceived;

    private CPICReturnCode cpicReturnCode;


}
