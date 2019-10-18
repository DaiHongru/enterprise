package com.freework.enterprise.dao;


import com.freework.enterprise.entity.EnterpriseCategory;

import java.util.List;

/**
 * @author daihongru
 */
public interface EnterpriseCategoryDao {
    /**
     * 查询一级分类
     *
     * @return
     */
    List<EnterpriseCategory> queryEnterpriseCategoryTop();

    /**
     * 根据parentId查询二级分类
     *
     * @param parentId
     * @return
     */
    List<EnterpriseCategory> queryEnterpriseCategorySecond(Integer parentId);

    /**
     * 根据id查询分类
     *
     * @param enterpriseCategoryId
     * @return
     */
    EnterpriseCategory queryEnterpriseCategoryById(Integer enterpriseCategoryId);
}
