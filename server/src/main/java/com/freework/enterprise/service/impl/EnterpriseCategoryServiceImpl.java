package com.freework.enterprise.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freework.common.loadon.cache.JedisUtil;
import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.common.loadon.result.enums.ResultStatusEnum;
import com.freework.common.loadon.result.util.ResultUtil;
import com.freework.enterprise.client.vo.EnterpriseCategoryVo;
import com.freework.enterprise.dao.EnterpriseCategoryDao;
import com.freework.enterprise.entity.EnterpriseCategory;
import com.freework.enterprise.exceptions.EnterpriseCategoryOperationException;
import com.freework.enterprise.service.EnterpriseCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author daihongru
 */
@Service
public class EnterpriseCategoryServiceImpl implements EnterpriseCategoryService {
    @Autowired(required = false)
    private EnterpriseCategoryDao enterpriseCategoryDao;
    @Autowired(required = false)
    private JedisUtil.Keys jedisKeys;
    @Autowired(required = false)
    private JedisUtil.Strings jedisStrings;

    private static Logger logger = LoggerFactory.getLogger(EnterpriseCategoryServiceImpl.class);

    @Override
    public ResultVo queryEnterpriseCategoryTop() {
        String key = ENTERPRISE_CATEGORY_TOP_KEY;
        List<EnterpriseCategory> enterpriseCategoryList = null;
        ObjectMapper mapper = new ObjectMapper();
        if (!jedisKeys.exists(key)) {
            enterpriseCategoryList = enterpriseCategoryDao.queryEnterpriseCategoryTop();
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(enterpriseCategoryList);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
                throw new EnterpriseCategoryOperationException(e.getMessage());
            }
            jedisStrings.set(key, jsonString);
        } else {
            String jsonString = jedisStrings.get(key);
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, EnterpriseCategory.class);
            try {
                enterpriseCategoryList = mapper.readValue(jsonString, javaType);
            } catch (JsonParseException e) {
                logger.error(e.getMessage());
                throw new EnterpriseCategoryOperationException(e.getMessage());
            } catch (JsonMappingException e) {
                logger.error(e.getMessage());
                throw new EnterpriseCategoryOperationException(e.getMessage());
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new EnterpriseCategoryOperationException(e.getMessage());
            }
        }
        if (enterpriseCategoryList.size() == 0 || enterpriseCategoryList == null) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        List<EnterpriseCategoryVo> enterpriseCategoryVoList = enterpriseCategoryList.stream().map(e -> {
            EnterpriseCategoryVo outPut = new EnterpriseCategoryVo();
            BeanUtils.copyProperties(e, outPut);
            return outPut;
        }).collect(Collectors.toList());
        return ResultUtil.success(enterpriseCategoryVoList);
    }

    @Override
    public ResultVo queryEnterpriseCategorySecond(Integer parentId) {
        if (parentId == null || parentId <= 0) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String key = ENTERPRISE_CATEGORY_SECOND_KEY + "_PARENT_" + parentId;
        List<EnterpriseCategory> enterpriseCategoryList = null;
        ObjectMapper mapper = new ObjectMapper();
        if (!jedisKeys.exists(key)) {
            enterpriseCategoryList = enterpriseCategoryDao.queryEnterpriseCategorySecond(parentId);
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(enterpriseCategoryList);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
                throw new EnterpriseCategoryOperationException(e.getMessage());
            }
            jedisStrings.set(key, jsonString);
        } else {
            String jsonString = jedisStrings.get(key);
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, EnterpriseCategory.class);
            try {
                enterpriseCategoryList = mapper.readValue(jsonString, javaType);
            } catch (JsonParseException e) {
                logger.error(e.getMessage());
                throw new EnterpriseCategoryOperationException(e.getMessage());
            } catch (JsonMappingException e) {
                logger.error(e.getMessage());
                throw new EnterpriseCategoryOperationException(e.getMessage());
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new EnterpriseCategoryOperationException(e.getMessage());
            }
        }
        if (enterpriseCategoryList.size() == 0 || enterpriseCategoryList == null) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        List<EnterpriseCategoryVo> enterpriseCategoryVoList = enterpriseCategoryList.stream().map(e -> {
            EnterpriseCategoryVo outPut = new EnterpriseCategoryVo();
            BeanUtils.copyProperties(e, outPut);
            return outPut;
        }).collect(Collectors.toList());
        return ResultUtil.success(enterpriseCategoryVoList);
    }
}
