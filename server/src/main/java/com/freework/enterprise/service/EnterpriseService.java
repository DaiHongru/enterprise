package com.freework.enterprise.service;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.enterprise.dto.ImageHolder;
import com.freework.enterprise.entity.Enterprise;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author daihongru
 */
@Service
public interface EnterpriseService {
    /**
     * 企业登录后在redis中超时时间单位，一天
     */
    int ENTERPRISE_REDIS_TIMEOUT_UNIT = 60 * 60 * 24;

    /**
     * 企业激活邮件验证码的redis key
     */
    String ENTERPRISE_EMAIL_ACTIVATION_CODE_KEY = "enterpriseEmailActivationCode";

    /**
     * 根据企业ID查询企业信息
     *
     * @param enterpriseId
     * @return
     */
    ResultVo queryEnterpriseById(Integer enterpriseId);

    /**
     * etoken自动登录
     *
     * @param token
     * @return
     */
    ResultVo etokenLogin(String token);

    /**
     * 企业登录验证
     *
     * @param enterprise
     * @param timeout
     * @param oldToken
     * @return
     */
    ResultVo loginCheck(Enterprise enterprise, int timeout, String oldToken);

    /**
     * 企业注册
     *
     * @param enterprise
     * @return
     */
    ResultVo registerEnterprise(Enterprise enterprise);

    /**
     * 验证邮箱激活码正确性
     *
     * @param enterpriseId
     * @param code
     * @param request
     * @return
     */
    ResultVo checkActivationEmailAddress(Integer enterpriseId, String code, HttpServletRequest request);

    /**
     * 查询邮箱或手机号码是否存在
     *
     * @param email
     * @param phone
     * @return
     */
    ResultVo queryEmailOrPhoneExist(String email, String phone);

    /**
     * 找回企业登录密码
     *
     * @param enterprise
     * @param evidence
     * @return
     */
    ResultVo retrievePassword(Enterprise enterprise, String evidence);

    /**
     * 重新发送邮件激活邮件
     *
     * @param token
     * @return
     */
    ResultVo resendActivatedMail(String token);

    /**
     * 修改企业地址
     *
     * @param newAddress
     * @param token
     * @return
     */
    ResultVo updateAddress(String newAddress, String token);

    /**
     * 修改企业官网
     *
     * @param newOfficialWebsite
     * @param token
     * @return
     */
    ResultVo updateOfficialWebsite(String newOfficialWebsite, String token);

    /**
     * 修改企业分类
     *
     * @param newEnterpriseCategoryId
     * @param token
     * @return
     */
    ResultVo updateEnterpriseCategory(Integer newEnterpriseCategoryId, String token);

    /**
     * 修改企业简介
     *
     * @param newIntroduce
     * @param token
     * @return
     */
    ResultVo updateEnterpriseIntroduce(String newIntroduce, String token);

    /**
     * 修改绑定手机
     *
     * @param newPhone
     * @param token
     * @return
     */
    ResultVo updatePhone(String newPhone, String token);

    /**
     * 修改绑定邮箱
     *
     * @param newEmail
     * @param token
     * @return
     */
    ResultVo updateEmail(String newEmail, String token);

    /**
     * 修改企业登录密码
     *
     * @param newPassword
     * @param token
     * @return
     */
    ResultVo updatePassword(String newPassword, String token);

    /**
     * 分页查询企业，按优先级降序
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultVo queryEnterpriseByPagination(Integer pageNum, Integer pageSize);

    /**
     * 退出登录时清除redis缓存的信息
     *
     * @param token
     * @return
     */
    ResultVo logout(String token);

    /**
     * 获取当前登录企业的信息
     *
     * @param token
     * @return
     */
    ResultVo getCurrentEnterpriseInfo(String token);

    /**
     * 向当前登录的企业发送验证短信
     *
     * @param token
     * @return
     */
    ResultVo sendVerificationSms(String token);

    /**
     * 向当前登录的企业发送验证邮件
     *
     * @param token
     * @return
     */
    ResultVo sendVerificationEmail(String token);

    /**
     * LOGO上传
     *
     * @param imageHolder
     * @param token
     * @return
     */
    ResultVo logoUpload(ImageHolder imageHolder, String token);
}
