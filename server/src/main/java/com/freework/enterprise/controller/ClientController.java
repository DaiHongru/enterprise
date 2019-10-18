package com.freework.enterprise.controller;

import com.freework.enterprise.client.vo.EnterpriseVo;
import com.freework.enterprise.service.ClientServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author daihongru
 */
@RestController
@RequestMapping(value = "client")
public class ClientController {
    @Autowired
    private ClientServer clientServer;

    /**
     * 根据企业ID获取企业信息
     *
     * @param enterpriseId
     * @return
     */
    @PostMapping("getenterprisebyid")
    public EnterpriseVo getEnterpriseById(@RequestBody Integer enterpriseId) {
        return clientServer.queryEnterpriseById(enterpriseId);
    }
}
