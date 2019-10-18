package com.freework.enterprise.service;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.enterprise.entity.Enterprise;
import org.springframework.stereotype.Service;

/**
 * @author daihongru
 */
@Service
public interface EmailService {
    String VERIFICATION_EMAIL_CODE_KEY = "verificationEmailCode";

    String CHECK_EVIDENCE="checkEvidence_";

    /**
     * 异步发送验证邮件
     *
     * @param email
     */
    void sendVerificationEmail(String email);

    /**
     * 查询验证码是否正确
     *
     * @param email
     * @param code
     * @return
     */
    ResultVo checkVerificationCode(String email, String code);

    /**
     * 异步执行发送用于激活邮箱的激活邮件
     *
     * @param enterprise
     */
    void sendActivatedMail(Enterprise enterprise);
}
