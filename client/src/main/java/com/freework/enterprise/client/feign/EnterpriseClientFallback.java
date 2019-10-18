package com.freework.enterprise.client.feign;

import com.freework.enterprise.client.vo.EnterpriseVo;
import org.springframework.stereotype.Component;

/**
 * @author daihongru
 */
@Component
public class EnterpriseClientFallback implements EnterpriseClient {

    @Override
    public EnterpriseVo getEnterpriseById(Integer enterpriseId) {
        EnterpriseVo enterpriseVo = new EnterpriseVo();
        enterpriseVo.setEnterpriseName("未获取到数据");
        return enterpriseVo;
    }
}
