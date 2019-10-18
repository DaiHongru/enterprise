package com.freework.enterprise.service;

import com.freework.enterprise.entity.MessageLog;
import org.springframework.stereotype.Service;

/**
 * @author daihongru
 */
@Service
public interface MessageLogService {
    String MESSAGELOG_KEY = "enterpriseProducerMessageLog";
    String MESSAGELOG_SMS_KEY = "enterpriseProducerMessageLogSms";
    String MESSAGELOG_EMAIL_KEY = "enterpriseProducerMessageLogEmail";

    /**
     * 把指定的redis中的MessageLog信息持久化到数据库，并删除该key
     *
     * @param messageLogKey
     * @param status
     */
    void persistence(String messageLogKey, Integer status);

    /**
     * 重新投递关于短信的消息
     *
     * @param messageLogKey
     */
    void resendSms(String messageLogKey, MessageLog messageLog);

    /**
     * 重新投递关于邮件的消息
     *
     * @param messageLogKey
     */
    void resendEmail(String messageLogKey, MessageLog messageLog);
}
