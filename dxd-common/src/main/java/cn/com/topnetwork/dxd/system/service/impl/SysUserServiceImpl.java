package cn.com.topnetwork.dxd.system.service.impl;

import cn.com.topnetwork.dxd.system.entity.SysUserVo;
import cn.com.topnetwork.dxd.system.mapper.SysUserMapper;
import cn.com.topnetwork.dxd.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUserVo queryByName(String userName) {
        SysUserVo queryVo = new SysUserVo();
        queryVo.setUsername(userName);
        return sysUserMapper.selectOne(queryVo);
    }

    @Override
    public SysUserVo queryByNameAndPwd(String userName, String password) {
        SysUserVo queryVo = new SysUserVo();
        queryVo.setUsername(userName);
        queryVo.setPassword(password);
        return sysUserMapper.selectOne(queryVo);
    }
}
