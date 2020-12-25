package cn.com.topnetwork.dxd.system.mapper;

import cn.com.topnetwork.dxd.system.entity.SysRolePermissionVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Set;

public interface SysRolePermissionMapper extends Mapper<SysRolePermissionVo> {

    /**
     * 根据角色id获取可用的权限编码
     *
     * @param roleId
     * @return
     */
    Set<String> getByRoleId(@Param("roleId") Long roleId);

}
