package cn.com.topnetwork.dxd.system.service;

import cn.com.topnetwork.dxd.system.entity.SysUserVo;

public interface ISysUserService {

    SysUserVo queryByName(String userName);

    SysUserVo queryByNameAndPwd(String userName,String password);

}
