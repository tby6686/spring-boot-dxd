package cn.com.topnetwork.dxd.system.service;

import cn.com.topnetwork.dxd.system.entity.SysLoginLogVo;
import cn.com.topnetwork.dxd.system.param.SysLoginLogPageParam;
import com.github.pagehelper.PageInfo;

public interface ISysLoginLogService {

    int saveSysLoginLog(SysLoginLogVo sysLoginLog);

    PageInfo<SysLoginLogVo> queryPageListByParam(SysLoginLogPageParam param);
}
