package com.freework.enterprise.service.impl;

import com.freework.enterprise.client.vo.EnterpriseVo;
import com.freework.enterprise.dao.EnterpriseDao;
import com.freework.enterprise.entity.Enterprise;
import com.freework.enterprise.service.ClientServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author daihongru
 */
@Service
public class ClientServerImpl implements ClientServer {
    @Autowired(required = false)
    private EnterpriseDao enterpriseDao;

    @Override
    public EnterpriseVo queryEnterpriseById(Integer enterpriseId) {
        Enterprise enterprise = new Enterprise();
        enterprise.setEnterpriseId(enterpriseId);
        List<Enterprise> enterpriseList = enterpriseDao.queryEnterpriseByRequirement(enterprise);
        if (enterpriseList == null || enterpriseList.size() == 0) {
            return null;
        }
        EnterpriseVo enterpriseVo = new EnterpriseVo();
        BeanUtils.copyProperties(enterpriseList.get(0), enterpriseVo);
        return enterpriseVo;
    }
}
