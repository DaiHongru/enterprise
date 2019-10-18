package com.freework.enterprise.dao;

import com.freework.enterprise.entity.Enterprise;

import java.util.List;


/**
 * @author daihongru
 */
public interface EnterpriseDao {
    /**
     * 验证账户名与密码，返回一个enterprise对象，仅登陆使用
     *
     * @param enterprise
     * @return enterprise
     */
    Enterprise loginEnterprise(Enterprise enterprise);

    /**
     * 根据传入的参数查询，返回一个enterprise列表
     *
     * @param enterprise
     * @return enterpriseList
     */
    List<Enterprise> queryEnterpriseByRequirement(Enterprise enterprise);

    /**
     * 企业用户注册
     *
     * @param enterprise
     * @return
     */
    int registerEnterprise(Enterprise enterprise);

    /**
     * 查询邮箱或手机号码是否存在
     *
     * @param enterprise
     * @return
     */
    Enterprise queryEmailOrPhoneExist(Enterprise enterprise);

    /**
     * 更改企业邮箱状态
     *
     * @param enterprise
     * @return
     */
    int updateEmailStatus(Enterprise enterprise);

    /**
     * 修改企业登录密码
     *
     * @param enterprise
     * @return
     */
    int updatePassword(Enterprise enterprise);

    /**
     * 修改企业logo信息
     *
     * @param enterprise
     * @return
     */
    int updateLogo(Enterprise enterprise);

    /**
     * 修改企业地址
     *
     * @param enterprise
     * @return
     */
    int updateAddress(Enterprise enterprise);

    /**
     * 修改企业官网
     *
     * @param enterprise
     * @return
     */
    int updateOfficialWebsite(Enterprise enterprise);

    /**
     * 修改企业类别
     *
     * @param enterprise
     * @return
     */
    int updateEnterpriseCategory(Enterprise enterprise);

    /**
     * 修改企业简介
     *
     * @param enterprise
     * @return
     */
    int updateEnterpriseIntroduce(Enterprise enterprise);

    /**
     * 修改绑定手机
     *
     * @param enterprise
     * @return
     */
    int updatePhone(Enterprise enterprise);

    /**
     * 修改绑定手机
     *
     * @param enterprise
     * @return
     */
    int updateEmail(Enterprise enterprise);

    /**
     * 查询所有enterprise，以priority降序排序
     *
     * @return enterpriseList
     */
    List<Enterprise> queryEnterpriseAll();
}
