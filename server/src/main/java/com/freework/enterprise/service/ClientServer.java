package com.freework.enterprise.service;

import com.freework.enterprise.client.vo.EnterpriseVo;
import org.springframework.stereotype.Service;

/**
 * @author daihongru
 */
@Service
public interface ClientServer {
    /**
     * 根据企业ID查询企业信息
     *
     * @param enterpriseId
     * @return
     */
    EnterpriseVo queryEnterpriseById(Integer enterpriseId);
}
