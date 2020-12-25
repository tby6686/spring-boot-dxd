package cn.com.topnetwork.dxd.system.service;

import cn.com.topnetwork.dxd.system.entity.SysOperationLogVo;
import cn.com.topnetwork.dxd.system.param.SysOperationLogPageParam;
import com.github.pagehelper.PageInfo;

public interface ISysOperationLogService {

    int saveSysOperationLog(SysOperationLogVo sysOperationLog);

    PageInfo<SysOperationLogVo> queryPageListByParam(SysOperationLogPageParam param);

}
