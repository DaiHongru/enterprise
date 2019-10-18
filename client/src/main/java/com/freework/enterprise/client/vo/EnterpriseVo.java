package com.freework.enterprise.client.vo;


import java.io.Serializable;
import java.util.Date;

/**
 * @author daihongru
 */
public class EnterpriseVo implements Serializable {

    /**
     * 企业编号，自增主键，唯一标识符，不可为空，新增时不传入
     */
    private Integer enterpriseId;

    /**
     * 企业状态，-1为冻结，0为审核中，1为正常，不可为空，新增时可不传入，默认为0
     */
    private Integer status;

    /**
     * 企业名称，不可为空
     */
    private String enterpriseName;

    /**
     * 企业优先级，默认为0
     */
    private Integer priority;

    /**
     * 企业邮箱
     */
    private String email;

    /**
     * 企业邮箱状态，0为待验证，1为正常，默认为0
     */
    private Integer emailStatus;

    /**
     * 企业注册手机号码，不可为空
     */
    private String phone;

    /**
     * 企业地址
     */
    private String address;

    /**
     * 企业logo图片，只储存相对地址
     */
    private String logo;

    /**
     * 企业官网
     */
    private String officialWebsite;

    /**
     * 企业官网状态，0审核中，1审核通过
     */
    private Integer officialWebsiteStatus;

    /**
     * 企业简介
     */
    private String introduce;

    /**
     * 企业分类ID
     */
    private Integer enterpriseCategoryId;

    /**
     * 企业分类名称
     */
    private String enterpriseCategoryName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最近修改时间
     */
    private Date lastEditTime;

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public Integer getEnterpriseCategoryId() {
        return enterpriseCategoryId;
    }

    public void setEnterpriseCategoryId(Integer enterpriseCategoryId) {
        this.enterpriseCategoryId = enterpriseCategoryId;
    }

    public Integer getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(Integer emailStatus) {
        this.emailStatus = emailStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getOfficialWebsite() {
        return officialWebsite;
    }

    public void setOfficialWebsite(String officialWebsite) {
        this.officialWebsite = officialWebsite;
    }

    public Integer getOfficialWebsiteStatus() {
        return officialWebsiteStatus;
    }

    public void setOfficialWebsiteStatus(Integer officialWebsiteStatus) {
        this.officialWebsiteStatus = officialWebsiteStatus;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getEnterpriseCategoryName() {
        return enterpriseCategoryName;
    }

    public void setEnterpriseCategoryName(String enterpriseCategoryName) {
        this.enterpriseCategoryName = enterpriseCategoryName;
    }
}
