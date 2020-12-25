package cn.com.topnetwork.dxd.system.service;

import java.util.Set;

public interface ISysRolePermissionService {

    Set<String> getByRoleId(Long roleId);

}
