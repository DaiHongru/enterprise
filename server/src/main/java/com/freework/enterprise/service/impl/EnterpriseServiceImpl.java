package com.freework.enterprise.service.impl;

import com.freework.common.loadon.cache.JedisUtil;
import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.common.loadon.result.enums.ResultStatusEnum;
import com.freework.common.loadon.result.util.ResultUtil;
import com.freework.common.loadon.util.DesUtil;
import com.freework.common.loadon.util.FileUtil;
import com.freework.common.loadon.util.JsonUtil;
import com.freework.common.loadon.util.PathUtil;
import com.freework.enterprise.client.key.EnterpriseRedisKey;
import com.freework.enterprise.client.vo.EnterpriseVo;
import com.freework.enterprise.dao.EnterpriseCategoryDao;
import com.freework.enterprise.dao.EnterpriseDao;
import com.freework.enterprise.dto.ImageHolder;
import com.freework.enterprise.entity.Enterprise;
import com.freework.enterprise.enums.EnterpriseStateEnum;
import com.freework.enterprise.exceptions.EnterpriseOperationException;
import com.freework.enterprise.service.EmailService;
import com.freework.enterprise.service.EnterpriseService;
import com.freework.enterprise.service.SmsService;
import com.freework.enterprise.util.ImageUtil;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author daihongru
 */
@Service
public class EnterpriseServiceImpl implements EnterpriseService {
    private static Logger logger = LoggerFactory.getLogger(EnterpriseServiceImpl.class);
    @Autowired(required = false)
    private EnterpriseDao enterpriseDao;
    @Autowired(required = false)
    private EnterpriseCategoryDao enterpriseCategoryDao;
    @Autowired(required = false)
    private JedisUtil.Keys jedisKeys;
    @Autowired(required = false)
    private JedisUtil.Strings jedisStrings;
    @Autowired
    private SmsService smsService;
    @Autowired
    private EmailService emailService;

    @Override
    public ResultVo queryEnterpriseById(Integer enterpriseId) {
        Enterprise enterprise = new Enterprise();
        enterprise.setEnterpriseId(enterpriseId);
        List<Enterprise> enterpriseList = enterpriseDao.queryEnterpriseByRequirement(enterprise);
        if (enterpriseList == null || enterpriseList.size() == 0) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        EnterpriseVo enterpriseVo = new EnterpriseVo();
        BeanUtils.copyProperties(enterpriseList.get(0), enterpriseVo);
        return ResultUtil.success(enterpriseVo);
    }

    @Override
    public ResultVo etokenLogin(String token) {
        String key = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(key)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        return ResultUtil.success();
    }

    @Override
    public ResultVo loginCheck(Enterprise enterprise, int timeout, String oldToken) {
        if (enterprise == null || enterprise.getPhone() == null || enterprise.getPassword() == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        if (timeout < 1) {
            timeout = 1;
        }
        if (StringUtils.isNotEmpty(oldToken)) {
            String oldKey = EnterpriseRedisKey.LOGIN_KEY + oldToken;
            if (jedisKeys.exists(oldKey)) {
                jedisKeys.del(oldKey);
            }
        }
        enterprise.setPassword(DesUtil.getEncryptString(enterprise.getPassword()));
        Enterprise e = enterpriseDao.loginEnterprise(enterprise);
        if (e == null) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        if (e.getStatus() == EnterpriseStateEnum.STOP.getState()) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = new EnterpriseVo();
        BeanUtils.copyProperties(e, enterpriseVo);
        enterpriseVo.setEnterpriseCategoryName(
                enterpriseCategoryDao.queryEnterpriseCategoryById(e.getEnterpriseCategoryId())
                        .getEnterpriseCategoryName());
        String token = UUID.randomUUID().toString();
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        String enterpriseStr = JsonUtil.objectToJson(enterpriseVo);
        jedisStrings.setEx(enterpriseKey, timeout * ENTERPRISE_REDIS_TIMEOUT_UNIT, enterpriseStr);
        return ResultUtil.success(token);
    }

    @Override
    public ResultVo registerEnterprise(Enterprise enterprise) {
        if (enterprise == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        enterprise.setStatus(EnterpriseStateEnum.CHECKING.getState());
        enterprise.setEmailStatus(EnterpriseStateEnum.CHECKING.getState());
        enterprise.setPassword(DesUtil.getEncryptString(enterprise.getPassword()));
        enterprise.setCreateTime(new Date());
        enterprise.setLastEditTime(new Date());
        try {
            int judgeNum = enterpriseDao.registerEnterprise(enterprise);
            if (judgeNum <= 0) {
                throw new EnterpriseOperationException("企业注册失败");
            }
        } catch (Exception e) {
            throw new EnterpriseOperationException("企业注册时发生异常:" + e.getMessage());
        }
        emailService.sendActivatedMail(enterprise);
        return ResultUtil.success();
    }

    @Override
    public ResultVo checkActivationEmailAddress(Integer enterpriseId, String code, HttpServletRequest request) {
        String key = ENTERPRISE_EMAIL_ACTIVATION_CODE_KEY + "_Id_" + enterpriseId;
        if (jedisKeys.exists(key)) {
            if (code.equals(jedisStrings.get(key))) {
                jedisKeys.del(key);
                Enterprise enterprise = new Enterprise();
                enterprise.setEnterpriseId(enterpriseId);
                enterprise.setEmailStatus(EnterpriseStateEnum.PASS.getState());
                enterprise.setLastEditTime(new Date());
                try {
                    int judgeNum = enterpriseDao.updateEmailStatus(enterprise);
                    if (judgeNum <= 0) {
                        throw new EnterpriseOperationException("修改失败");
                    }
                } catch (Exception e) {
                    throw new EnterpriseOperationException("updateEmailStatus error:" + e.getMessage());
                }
                String token = request.getHeader("etoken");
                if (StringUtils.isNotEmpty(token)) {
                    String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
                    if (jedisKeys.exists(enterpriseKey)) {
                        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
                        enterpriseVo.setEmailStatus(EnterpriseStateEnum.PASS.getState());
                        setCurrentEnterpriseVo(enterpriseVo, enterpriseKey);
                    }
                }
                return ResultUtil.success();
            }
        }
        return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
    }

    @Override
    public ResultVo queryEmailOrPhoneExist(String email, String phone) {
        Enterprise enterprise = new Enterprise();
        enterprise.setPhone(phone);
        enterprise.setEmail(email);
        Enterprise e = enterpriseDao.queryEmailOrPhoneExist(enterprise);
        if (e == null) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        } else {
            return ResultUtil.success();
        }
    }

    @Override
    public ResultVo retrievePassword(Enterprise enterprise, String evidence) {
        if (StringUtils.isEmpty(evidence)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        if (!jedisKeys.exists(SmsService.CHECK_EVIDENCE + evidence)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        if (enterprise.getPhone() == null || enterprise.getPassword() == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        if (!enterprise.getPhone().equals(jedisStrings.get(SmsService.CHECK_EVIDENCE + evidence))) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        enterprise.setPassword(DesUtil.getEncryptString(enterprise.getPassword()));
        enterprise.setLastEditTime(new Date());
        try {
            int judgeNum = enterpriseDao.updatePassword(enterprise);
            if (judgeNum <= 0) {
                throw new EnterpriseOperationException("修改企业密码失败");
            }
        } catch (Exception e) {
            throw new EnterpriseOperationException("企业找回密码时发生异常:" + e.getMessage());
        }
        return ResultUtil.success();
    }

    @Override
    public ResultVo resendActivatedMail(String token) {
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        String enterpriseVoStr = jedisStrings.get(enterpriseKey);
        EnterpriseVo enterpriseVo = JsonUtil.jsonToObject(enterpriseVoStr, EnterpriseVo.class);
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseVo, enterprise);
        emailService.sendActivatedMail(enterprise);
        return ResultUtil.success();
    }

    @Override
    public ResultVo updateAddress(String newAddress, String token) {
        if (StringUtils.isEmpty(newAddress)) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        enterpriseVo.setAddress(newAddress);
        enterpriseVo.setLastEditTime(new Date());
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseVo, enterprise);
        try {
            int judgeNum = enterpriseDao.updateAddress(enterprise);
            if (judgeNum <= 0) {
                throw new EnterpriseOperationException("修改企业地址失败");
            }
        } catch (Exception e) {
            throw new EnterpriseOperationException("企业修改地址时发生异常:" + e.getMessage());
        }
        setCurrentEnterpriseVo(enterpriseVo, enterpriseKey);
        return ResultUtil.success();
    }

    @Override
    public ResultVo updateOfficialWebsite(String newOfficialWebsite, String token) {
        if (StringUtils.isEmpty(newOfficialWebsite)) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        enterpriseVo.setOfficialWebsite(newOfficialWebsite);
        enterpriseVo.setOfficialWebsiteStatus(EnterpriseStateEnum.CHECKING.getState());
        enterpriseVo.setLastEditTime(new Date());
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseVo, enterprise);
        try {
            int judgeNum = enterpriseDao.updateOfficialWebsite(enterprise);
            if (judgeNum <= 0) {
                throw new EnterpriseOperationException("修改企业官网失败");
            }
        } catch (Exception e) {
            throw new EnterpriseOperationException("企业修改官网时发生异常:" + e.getMessage());
        }
        setCurrentEnterpriseVo(enterpriseVo, enterpriseKey);
        return ResultUtil.success();
    }

    @Override
    public ResultVo updateEnterpriseCategory(Integer newEnterpriseCategoryId, String token) {
        if (newEnterpriseCategoryId == null || newEnterpriseCategoryId <= 0) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        enterpriseVo.setEnterpriseCategoryId(newEnterpriseCategoryId);
        enterpriseVo.setEnterpriseCategoryName(
                enterpriseCategoryDao.queryEnterpriseCategoryById(newEnterpriseCategoryId)
                        .getEnterpriseCategoryName());
        enterpriseVo.setLastEditTime(new Date());
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseVo, enterprise);
        try {
            int judgeNum = enterpriseDao.updateEnterpriseCategory(enterprise);
            if (judgeNum <= 0) {
                throw new EnterpriseOperationException("修改企业分类失败");
            }
        } catch (Exception e) {
            throw new EnterpriseOperationException("企业修改分类时发生异常:" + e.getMessage());
        }
        setCurrentEnterpriseVo(enterpriseVo, enterpriseKey);
        return ResultUtil.success();
    }

    @Override
    public ResultVo updateEnterpriseIntroduce(String newIntroduce, String token) {
        if (StringUtils.isEmpty(newIntroduce)) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        enterpriseVo.setIntroduce(newIntroduce);
        enterpriseVo.setLastEditTime(new Date());
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseVo, enterprise);
        try {
            int judgeNum = enterpriseDao.updateEnterpriseIntroduce(enterprise);
            if (judgeNum <= 0) {
                throw new EnterpriseOperationException("修改企业简介失败");
            }
        } catch (Exception e) {
            throw new EnterpriseOperationException("企业修改简介时发生异常:" + e.getMessage());
        }
        setCurrentEnterpriseVo(enterpriseVo, enterpriseKey);
        return ResultUtil.success();
    }

    @Override
    public ResultVo updatePhone(String newPhone, String token) {
        if (StringUtils.isEmpty(newPhone)) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        enterpriseVo.setPhone(newPhone);
        enterpriseVo.setLastEditTime(new Date());
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseVo, enterprise);
        try {
            int judgeNum = enterpriseDao.updatePhone(enterprise);
            if (judgeNum <= 0) {
                throw new EnterpriseOperationException("修改企业绑定手机失败");
            }
        } catch (Exception e) {
            throw new EnterpriseOperationException("企业修改绑定手机时发生异常:" + e.getMessage());
        }
        return logout(token);
    }

    @Override
    public ResultVo updateEmail(String newEmail, String token) {
        if (StringUtils.isEmpty(newEmail)) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        enterpriseVo.setEmail(newEmail);
        enterpriseVo.setEmailStatus(0);
        enterpriseVo.setLastEditTime(new Date());
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseVo, enterprise);
        try {
            int judgeNum = enterpriseDao.updateEmail(enterprise);
            if (judgeNum <= 0) {
                throw new EnterpriseOperationException("修改失败");
            }
        } catch (Exception e) {
            throw new EnterpriseOperationException("企业修改绑定邮箱时发生异常:" + e.getMessage());
        }
        emailService.sendActivatedMail(enterprise);
        setCurrentEnterpriseVo(enterpriseVo, enterpriseKey);
        return ResultUtil.success();
    }

    @Override
    public ResultVo updatePassword(String newPassword, String token) {
        if (StringUtils.isEmpty(newPassword)) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        enterpriseVo.setLastEditTime(new Date());
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseVo, enterprise);
        enterprise.setPassword(DesUtil.getEncryptString(newPassword));
        try {
            int judgeNum = enterpriseDao.updatePassword(enterprise);
            if (judgeNum <= 0) {
                throw new EnterpriseOperationException("修改失败");
            }
        } catch (Exception e) {
            throw new EnterpriseOperationException("企业修改密码时发生异常:" + e.getMessage());
        }
        return logout(token);
    }

    @Override
    public ResultVo queryEnterpriseByPagination(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        List<Enterprise> enterpriseList = null;
        PageHelper.startPage(pageNum, pageSize);
        enterpriseList = enterpriseDao.queryEnterpriseAll();
        if (enterpriseList == null || enterpriseList.size() == 0) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        return ResultUtil.success(enterpriseList);
    }

    @Override
    public ResultVo logout(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
            if (jedisKeys.exists(enterpriseKey)) {
                jedisKeys.del(enterpriseKey);
            }
        }
        return ResultUtil.success();
    }

    @Override
    public ResultVo getCurrentEnterpriseInfo(String token) {
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        String enterpriseVoStr = jedisStrings.get(enterpriseKey);
        return ResultUtil.success(getCurrentEnterpriseVo(enterpriseKey));
    }

    @Override
    public ResultVo sendVerificationSms(String token) {
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        smsService.sendVerificationSms(enterpriseVo.getPhone());
        return ResultUtil.success();
    }

    @Override
    public ResultVo sendVerificationEmail(String token) {
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        emailService.sendVerificationEmail(enterpriseVo.getEmail());
        return ResultUtil.success();
    }


    @Override
    public ResultVo logoUpload(ImageHolder imageHolder, String token) {
        if (imageHolder == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());
        String fileName = "enterpriseLogo_" + timeStr + "_" + enterpriseVo.getEnterpriseId();
        String extension = FileUtil.getFileExtension(imageHolder.getImageName());
        String targetAddr = PathUtil.getEnterpriseLogoPath(enterpriseVo.getEnterpriseId());
        FileUtil.mkdirPath(targetAddr);
        String path = targetAddr + fileName + extension;
        ImageUtil.storageImage(path, imageHolder);
        if (enterpriseVo.getLogo() != null) {
            StringBuffer buffer = new StringBuffer(enterpriseVo.getLogo());
            buffer.delete(0, 15);
            FileUtil.deleteFileOrPath(buffer.toString());
        }
        enterpriseVo.setLogo("/localresources" + path);
        enterpriseVo.setLastEditTime(new Date());
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseVo, enterprise);
        try {
            int judgeNum = enterpriseDao.updateLogo(enterprise);
            if (judgeNum <= 0) {
                logger.error("上传企业logo时储存文件路径失败");
                throw new EnterpriseOperationException("上传企业logo时储存文件路径失败");
            }
        } catch (Exception e) {
            logger.error("上传企业logo时储存文件路径异常:" + e.getMessage());
            throw new EnterpriseOperationException("上传企业logo时储存文件路径异常:" + e.getMessage());
        }
        setCurrentEnterpriseVo(enterpriseVo, enterpriseKey);
        return ResultUtil.success();
    }

    /**
     * 获取当前登录企业
     *
     * @param key
     * @return
     */
    private EnterpriseVo getCurrentEnterpriseVo(String key) {
        String enterpriseStr = jedisStrings.get(key);
        EnterpriseVo enterpriseVo = JsonUtil.jsonToObject(enterpriseStr, EnterpriseVo.class);
        return enterpriseVo;
    }

    /**
     * 设置当前登录企业的信息
     *
     * @param enterpriseVo
     * @param key
     */
    private void setCurrentEnterpriseVo(EnterpriseVo enterpriseVo, String key) {
        String enterpriseStr = JsonUtil.objectToJson(enterpriseVo);
        jedisStrings.set(key, enterpriseStr);
    }
}
