package com.freework.enterprise.entity;


import java.util.Date;

/**
 * @author daihongru
 */
public class EnterpriseCategory {

    /**
     * 企业分类编号，自增主键，唯一标识符，不可为空，新增时不传入
     */
    private Integer enterpriseCategoryId;

    /**
     * 分类名称，不可为空
     */
    private String enterpriseCategoryName;

    /**
     * 上级分类，一级分类无上级属性
     */
    private Integer parentId;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最近修改时间
     */
    private Date lastEditTime;

    public Integer getEnterpriseCategoryId() {
        return enterpriseCategoryId;
    }

    public void setEnterpriseCategoryId(Integer enterpriseCategoryId) {
        this.enterpriseCategoryId = enterpriseCategoryId;
    }

    public String getEnterpriseCategoryName() {
        return enterpriseCategoryName;
    }

    public void setEnterpriseCategoryName(String enterpriseCategoryName) {
        this.enterpriseCategoryName = enterpriseCategoryName;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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
}
