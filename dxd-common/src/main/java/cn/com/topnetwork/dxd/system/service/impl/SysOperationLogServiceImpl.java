package cn.com.topnetwork.dxd.system.service.impl;

import cn.com.topnetwork.dxd.system.entity.SysLoginLogVo;
import cn.com.topnetwork.dxd.system.entity.SysOperationLogVo;
import cn.com.topnetwork.dxd.system.mapper.SysOperationLogMapper;
import cn.com.topnetwork.dxd.system.param.SysOperationLogPageParam;
import cn.com.topnetwork.dxd.system.service.ISysOperationLogService;
import cn.com.topnetwork.dxd.util.PageUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysOperationLogServiceImpl implements ISysOperationLogService {

    @Autowired
    private SysOperationLogMapper sysOperationLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveSysOperationLog(SysOperationLogVo sysOperationLog) {
        return sysOperationLogMapper.insert(sysOperationLog);
    }

    @Override
    public PageInfo<SysOperationLogVo> queryPageListByParam(SysOperationLogPageParam param) {
        PageUtil.initPage(param);
        List<SysOperationLogVo> allList =  sysOperationLogMapper.selectAll();
        return new PageInfo<>(allList);
    }
}
