package cn.com.topnetwork.dxd.system.service.impl;

import cn.com.topnetwork.dxd.system.entity.SysLoginLogVo;
import cn.com.topnetwork.dxd.system.mapper.SysLoginLogMapper;
import cn.com.topnetwork.dxd.system.param.SysLoginLogPageParam;
import cn.com.topnetwork.dxd.system.service.ISysLoginLogService;
import cn.com.topnetwork.dxd.util.PageUtil;
import cn.com.topnetwork.dxd.util.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysLoginLogServiceImpl implements ISysLoginLogService {


    @Autowired
    private SysLoginLogMapper sysLoginLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveSysLoginLog(SysLoginLogVo sysLoginLog) {
        return sysLoginLogMapper.insert(sysLoginLog);
    }

    @Override
    public PageInfo<SysLoginLogVo> queryPageListByParam(SysLoginLogPageParam param) {
        PageUtil.initPage(param);
        List<SysLoginLogVo> allList =  sysLoginLogMapper.selectAll();
        return new PageInfo<>(allList);
    }
}
