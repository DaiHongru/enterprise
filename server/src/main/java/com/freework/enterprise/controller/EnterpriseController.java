package com.freework.enterprise.controller;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.common.loadon.result.enums.ResultStatusEnum;
import com.freework.common.loadon.result.util.ResultUtil;
import com.freework.common.loadon.util.HttpServletRequestUtil;
import com.freework.enterprise.dto.ImageHolder;
import com.freework.enterprise.entity.Enterprise;
import com.freework.enterprise.service.EmailService;
import com.freework.enterprise.service.EnterpriseService;
import com.freework.enterprise.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author daihongru
 */
@RestController
public class EnterpriseController {
    private static Logger logger = LoggerFactory.getLogger(EnterpriseController.class);
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private EmailService emailService;

    /**
     * 根据企业ID获取企业信息
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping("id/{enterpriseId}")
    public ResultVo getEnterpriseById(@PathVariable Integer enterpriseId) {
        return enterpriseService.queryEnterpriseById(enterpriseId);
    }

    /**
     * 分页查询企业信息
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "page/{pageNum}/{pageSize}")
    public ResultVo getEnterpriseByPagination(@PathVariable Integer pageNum,
                                              @PathVariable Integer pageSize) {
        return enterpriseService.queryEnterpriseByPagination(pageNum, pageSize);
    }

    /**
     * 通过token自动登录
     *
     * @param request
     * @return
     */
    @GetMapping(value = "current/login")
    public ResultVo etokenLogin(HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.etokenLogin(token);
    }

    /**
     * 企业登录验证
     *
     * @param enterprise
     * @return
     */
    @PostMapping(value = "login")
    public ResultVo loginCheck(@RequestBody Enterprise enterprise, HttpServletRequest request) {
        int timeout = request.getIntHeader("timeout");
        String oldToken = request.getHeader("etoken");
        return enterpriseService.loginCheck(enterprise, timeout, oldToken);
    }

    /**
     * 企业注册
     *
     * @param enterprise
     * @return
     */
    @PostMapping(value = "register")
    public ResultVo registerEnterprise(@RequestBody Enterprise enterprise) {
        return enterpriseService.registerEnterprise(enterprise);
    }

    /**
     * 验证邮箱激活码正确性，正确则将邮箱状态修改为1
     * 如果当前已登录，则更新redis中的状态
     *
     * @param request
     * @return
     */
    @GetMapping(value = "email/{enterpriseId}/{code}")
    public ResultVo checkActivationEmailAddress(@PathVariable Integer enterpriseId,
                                                @PathVariable String code,
                                                HttpServletRequest request) {
        return enterpriseService.checkActivationEmailAddress(enterpriseId, code, request);
    }

    /**
     * 注销登录
     *
     * @return
     */
    @GetMapping(value = "logout")
    public ResultVo logout(HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.logout(token);
    }

    /**
     * 查询邮箱或手机号码是否存在
     * 如需要存在为false，如注册时查询，则需要传入不为空的inversion参数，即可置反
     *
     * @param request
     * @return
     */
    @PostMapping(value = "contact/exist")
    public ResultVo queryEmailOrPhoneExist(HttpServletRequest request) {
        String email = HttpServletRequestUtil.getString(request, "email");
        String phone = HttpServletRequestUtil.getString(request, "phone");
        String inversion = HttpServletRequestUtil.getString(request, "inversion");
        ResultVo resultVo = enterpriseService.queryEmailOrPhoneExist(email, phone);
        if (inversion != null) {
            resultVo.setSuccess(!resultVo.isSuccess());
        }
        return resultVo;
    }

    /**
     * 获取当前登录的企业信息
     *
     * @return
     */
    @GetMapping(value = "current/info")
    public ResultVo getCurrentEnterpriseInfo(HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.getCurrentEnterpriseInfo(token);
    }

    /**
     * 找回密码
     *
     * @param enterprise
     * @return
     */
    @PutMapping(value = "retrieve/password")
    public ResultVo retrievePassword(@RequestBody Enterprise enterprise, HttpServletRequest request) {
        String evidence = request.getHeader("evidence");
        return enterpriseService.retrievePassword(enterprise, evidence);
    }

    /**
     * 发送企业邮箱激活邮件
     *
     * @return
     */
    @GetMapping(value = "current/send/email")
    public ResultVo resendActivatedMail(HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.resendActivatedMail(token);
    }

    /**
     * 修改公司地址
     *
     * @return
     */
    @PutMapping(value = "current/address")
    public ResultVo updateAddress(String newAddress, HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.updateAddress(newAddress, token);
    }

    /**
     * 修改企业官网
     *
     * @return
     */
    @PutMapping(value = "current/officialwebsite")
    public ResultVo updateOfficialWebsite(String newOfficialWebsite, HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.updateOfficialWebsite(newOfficialWebsite, token);
    }

    /**
     * 修改企业分类
     *
     * @return
     */
    @PutMapping(value = "current/category")
    public ResultVo updateEnterpriseCategory(Integer newEnterpriseCategoryId, HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.updateEnterpriseCategory(newEnterpriseCategoryId, token);
    }

    /**
     * 修改企业简介
     *
     * @return
     */
    @PutMapping(value = "current/introduce")
    public ResultVo updateEnterpriseIntroduce(String newIntroduce, HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.updateEnterpriseIntroduce(newIntroduce, token);
    }

    /**
     * 修改绑定手机
     *
     * @return
     */
    @PutMapping(value = "current/phone")
    public ResultVo updatePhone(String newPhone, HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.updatePhone(newPhone, token);
    }

    /**
     * 修改绑定邮箱
     *
     * @return
     */
    @PutMapping(value = "current/email")
    public ResultVo updateEmail(String newEmail, HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.updateEmail(newEmail, token);
    }

    /**
     * 修改登陆密码
     *
     * @return
     */
    @PutMapping(value = "current/password")
    public ResultVo updatePassword(String newPassword, HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.updatePassword(newPassword, token);
    }

    /**
     * 向当前登陆的用户发送验证短信
     *
     * @param request
     */
    @GetMapping(value = "current/sms")
    public ResultVo sendCurrentVerificationSms(HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.sendVerificationSms(token);
    }

    /**
     * 发送验证短信
     *
     * @param phone
     * @return
     */
    @GetMapping(value = "sms/{phone}")
    public ResultVo sendVerificationSms(@PathVariable String phone) {
        smsService.sendVerificationSms(phone);
        return ResultUtil.success();
    }

    /**
     * 查询短信验证码是否正确
     *
     * @param phone
     * @param code
     * @return
     */
    @GetMapping(value = "sms/{phone}/{code}")
    public ResultVo checkSmsVerificationCode(@PathVariable String phone,
                                             @PathVariable String code) {
        int codeLength = 6;
        if (code.length() == codeLength && phone != null) {
            return smsService.checkVerificationCode(phone, code);
        } else {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
    }

    /**
     * 向当前登陆的用户发送验证码邮件
     *
     * @param request
     */
    @GetMapping(value = "current/email")
    public ResultVo sendCurrentVerificationEmail(HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return enterpriseService.sendVerificationEmail(token);
    }

    /**
     * 发送验证邮件
     *
     * @param email
     */
    @GetMapping(value = "email/{email}")
    public ResultVo sendVerificationEmail(@PathVariable String email) {
        emailService.sendVerificationEmail(email);
        return ResultUtil.success();
    }

    /**
     * 查询邮箱验证码是否正确
     *
     * @param email
     * @param code
     * @return
     */
    @GetMapping(value = "email/code/{email}/{code}")
    public ResultVo checkVerificationCode(@PathVariable String email,
                                          @PathVariable String code) {
        int codeLength = 6;
        if (code.length() == codeLength && email != null) {
            return emailService.checkVerificationCode(email, code);
        } else {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
    }


    /**
     * 企业logo上传
     *
     * @return
     */
    @PostMapping(value = "current/logo")
    public ResultVo logoUpload(MultipartHttpServletRequest request) throws IOException {
        MultipartFile logo = request.getFile("logo");
        ImageHolder imageHolder = new ImageHolder(logo.getOriginalFilename(), logo.getInputStream());
        String token = request.getHeader("etoken");
        return enterpriseService.logoUpload(imageHolder, token);
    }
}
