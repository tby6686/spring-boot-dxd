package cn.com.topnetwork.dxd.system.service.impl;

import cn.com.topnetwork.dxd.system.entity.SysRoleVo;
import cn.com.topnetwork.dxd.system.mapper.SysRoleMapper;
import cn.com.topnetwork.dxd.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl implements ISysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public SysRoleVo getById(Long id) {
        return sysRoleMapper.selectByPrimaryKey(id);
    }
}
