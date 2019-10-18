package com.freework.enterprise.client.feign;

import com.freework.enterprise.client.vo.EnterpriseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author daihongru
 */
@FeignClient(name = "ENTERPRISE",fallback = EnterpriseClientFallback.class)
public interface EnterpriseClient {
    /**
     * 根据企业ID获取企业信息
     *
     * @param enterpriseId
     * @return
     */
    @PostMapping("/client/getenterprisebyid")
    EnterpriseVo getEnterpriseById(@RequestBody Integer enterpriseId);
}
