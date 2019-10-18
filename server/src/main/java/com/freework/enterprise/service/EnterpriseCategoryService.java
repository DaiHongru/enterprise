package com.freework.enterprise.service;

import com.freework.common.loadon.result.entity.ResultVo;
import org.springframework.stereotype.Service;

/**
 * @author daihongru
 */
@Service
public interface EnterpriseCategoryService {
    String ENTERPRISE_CATEGORY_TOP_KEY = "enterpriseCategoryTop";
    String ENTERPRISE_CATEGORY_SECOND_KEY = "enterpriseCategorySecond";

    /**
     * 查询一级分类
     *
     * @return
     */
    ResultVo queryEnterpriseCategoryTop();

    /**
     * 查询二级分类
     *
     * @param parentId
     * @return
     */
    ResultVo queryEnterpriseCategorySecond(Integer parentId);
}
