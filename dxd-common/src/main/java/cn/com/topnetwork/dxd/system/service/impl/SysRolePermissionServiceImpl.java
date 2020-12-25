package cn.com.topnetwork.dxd.system.service.impl;

import cn.com.topnetwork.dxd.system.entity.SysRolePermissionVo;
import cn.com.topnetwork.dxd.system.mapper.SysRolePermissionMapper;
import cn.com.topnetwork.dxd.system.service.ISysRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SysRolePermissionServiceImpl implements ISysRolePermissionService {

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public Set<String> getByRoleId(Long roleId) {
        return sysRolePermissionMapper.getByRoleId(roleId);
    }
}
