package com.iaspec.uniongatewayserver.service;

import COM.ibm.eNetwork.cpic.CPICReturnCode;
import com.iaspec.uniongatewayserver.exception.ServiceException;
import com.iaspec.uniongatewayserver.model.AcceptResult;


/**
 * @author Flamenco.xxx
 * @date 2022/8/19  9:09
 */
public interface CpicService {


    void init() throws InterruptedException;
    void sendMessage2MainFrame(String systemDestName,byte[] data) throws InterruptedException;

    void acceptMessageFromMainFrame();

    void sendMsg2Union(AcceptResult acceptResult) throws Throwable;

    void autoSendMsgback() throws Throwable;

    void sendMsgBack(String content) throws Throwable;

    void checkReturnCode(CPICReturnCode returnCode) throws ServiceException;
}
