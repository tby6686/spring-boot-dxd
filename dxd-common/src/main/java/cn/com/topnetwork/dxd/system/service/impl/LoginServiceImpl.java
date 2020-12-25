package cn.com.topnetwork.dxd.system.service.impl;

import cn.com.topnetwork.dxd.api.ApiCode;
import cn.com.topnetwork.dxd.api.ApiResult;
import cn.com.topnetwork.dxd.base.cache.ICache;
import cn.com.topnetwork.dxd.constant.enu.StateEnum;
import cn.com.topnetwork.dxd.constant.properties.JwtProperties;
import cn.com.topnetwork.dxd.system.entity.LoginSysUserVo;
import cn.com.topnetwork.dxd.system.entity.SysDepartmentVo;
import cn.com.topnetwork.dxd.system.entity.SysRoleVo;
import cn.com.topnetwork.dxd.system.entity.SysUserVo;
import cn.com.topnetwork.dxd.system.entity.convert.SysUserConvert;
import cn.com.topnetwork.dxd.system.param.LoginParam;
import cn.com.topnetwork.dxd.system.service.ILoginService;
import cn.com.topnetwork.dxd.system.service.ISysDepartmentService;
import cn.com.topnetwork.dxd.system.service.ISysRolePermissionService;
import cn.com.topnetwork.dxd.system.service.ISysRoleService;
import cn.com.topnetwork.dxd.util.CacheUtil;
import cn.com.topnetwork.dxd.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Slf4j
@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private ICache cache;
    @Autowired
    private ISysDepartmentService sysDepartmentService;
    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
    private ISysRolePermissionService sysRolePermissionService;

    @Override
    public ApiResult login(LoginParam loginParam, HttpServletRequest request, HttpServletResponse response) {
        // 获取请求主体
        Subject subject = SecurityUtils.getSubject();
        try {
            // 将用户请求参数封装
            UsernamePasswordToken token = new UsernamePasswordToken(loginParam.getUsername(), loginParam.getPassword());
            /**
             * 直接提交给Shiro处理，进入内部验证，如果验证失败，返回AuthenticationException <br/>
             * 如果通过，就将全部认证信息关联到 此Subject上，subject.getPrincipal()将非空，且subject.isAuthenticated()为True
             */
            subject.login(token);

            SysUserVo user = (SysUserVo)subject.getPrincipal();

            //用户并发登陆 可能产生多个token
            String jwtToken = JwtUtil.generateToken(user.getId(),user.getUsername());

            //存放缓存组件中
            //1.当前登陆用户使用的token
            CacheUtil.cacheLoginSysUserToken(user.getUsername(),jwtToken,true);

            //2.当前登陆用户对象 主要是权限信息
            // 将系统用户对象转换成登录用户对象
            LoginSysUserVo loginSysUserVo = SysUserConvert.INSTANCE.sysUserToLoginSysUserVo(user);

            // 获取部门
            SysDepartmentVo sysDepartment = sysDepartmentService.getById(user.getDepartmentId());
            if (sysDepartment == null) {
                throw new AuthenticationException("部门不存在");
            }
            if (!StateEnum.ENABLE.getCode().equals(sysDepartment.getState())) {
                throw new AuthenticationException("部门已禁用");
            }
            loginSysUserVo.setDepartmentId(sysDepartment.getId())
                    .setDepartmentName(sysDepartment.getName());

            // 获取当前用户角色
            Long roleId = user.getRoleId();
            SysRoleVo sysRole = sysRoleService.getById(roleId);
            if (sysRole == null) {
                throw new AuthenticationException("角色不存在");
            }
            if (StateEnum.DISABLE.getCode().equals(sysRole.getState())) {
                throw new AuthenticationException("角色已禁用");
            }
            loginSysUserVo.setRoleId(sysRole.getId())
                    .setRoleName(sysRole.getName())
                    .setRoleCode(sysRole.getCode());

            // 获取当前用户权限
            Set<String> rolePermissionCodes = sysRolePermissionService.getByRoleId(roleId);
            if (CollectionUtils.isEmpty(rolePermissionCodes)) {
                throw new AuthenticationException("权限列表不能为空");
            }
            loginSysUserVo.setPermissionCodes(rolePermissionCodes);

            //缓存登陆用户对象信息
            CacheUtil.cacheLoginSysUser(loginSysUserVo);

            // 写入响应信息返回
            response.setHeader(JwtProperties.tokenName, jwtToken);

            log.info("User {} get jwtToken success,token {}", user.getUsername(), jwtToken);
            return ApiResult.success("登陆获取token成功",jwtToken);

        } catch (AuthenticationException e) {
            // 如果校验失败，shiro会抛出异常，返回客户端失败
            log.error("User:{} get token fail,AuthenticationException>>", loginParam.getUsername(), e);
            return ApiResult.result(ApiCode.UNAUTHENTICATED_EXCEPTION);
        } catch (Exception e) {
            log.error("User:{} get token fail,Exception>> ", loginParam.getUsername(), e);
            return ApiResult.result(ApiCode.UNAUTHENTICATED_EXCEPTION);
        }
    }


    @Override
    public ApiResult logout(HttpServletRequest request) {
        try{
            Subject subject = SecurityUtils.getSubject();
            //注销
            subject.logout();
            // 获取token
            String token = JwtUtil.getTokenFromRequest(request);
            String username = JwtUtil.getUsername(token);
            // 删除缓存信息>>当前登陆用户token缓存信息(顺序不可颠倒)
            CacheUtil.delLoginSysUserTokenByKey(username,token,true);
            // 删除缓存信息>>当前登陆用户信息(顺序不可颠倒)
            CacheUtil.delLoginSysUserByUsername(username);
            log.info("登出成功,username:{},token:{}", username, token);
            return ApiResult.success(username+"登出成功!");
        }catch (Exception e){
            log.error("logout fail,Exception>> ", e);
            return ApiResult.result(ApiCode.FAIL);
        }
    }
}