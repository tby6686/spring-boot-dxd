package cn.com.topnetwork.dxd.system.service.impl;

import cn.com.topnetwork.dxd.system.entity.SysDepartmentVo;
import cn.com.topnetwork.dxd.system.entity.SysUserVo;
import cn.com.topnetwork.dxd.system.mapper.SysDepartmentMapper;
import cn.com.topnetwork.dxd.system.mapper.SysUserMapper;
import cn.com.topnetwork.dxd.system.service.ISysDepartmentService;
import cn.com.topnetwork.dxd.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysDepartmentServiceImpl implements ISysDepartmentService {

    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;

    @Override
    public SysDepartmentVo getById(Long id) {
        return sysDepartmentMapper.selectByPrimaryKey(id);
    }
}
