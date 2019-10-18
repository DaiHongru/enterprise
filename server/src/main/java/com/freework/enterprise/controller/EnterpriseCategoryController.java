package com.freework.enterprise.controller;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.enterprise.service.EnterpriseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author daihongru
 */
@RestController
@RequestMapping(value = "category")
public class EnterpriseCategoryController {
    @Autowired
    private EnterpriseCategoryService enterpriseCategoryService;

    /**
     * 查询一级企业分类
     *
     * @return
     */
    @GetMapping(value = "tops")
    public ResultVo queryEnterpriseCategoryTop() {
        return enterpriseCategoryService.queryEnterpriseCategoryTop();
    }

    /**
     * 查询二级企业分类
     *
     * @return
     */
    @GetMapping(value = "seconds/{parentId}")
    public ResultVo queryEnterpriseCategorySecond(@PathVariable Integer parentId) {
        return enterpriseCategoryService.queryEnterpriseCategorySecond(parentId);
    }
}
