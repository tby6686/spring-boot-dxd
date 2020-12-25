package cn.com.topnetwork.dxd.shiro.realm;

import cn.com.topnetwork.dxd.system.entity.SysUserVo;
import cn.com.topnetwork.dxd.system.service.ISysUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * 
 * <p>
 * Db认证数据源
 * </p>
 *
 * @author tby
 * @since 2020年2月16日
 */
public class DbRealm extends AuthorizingRealm {

    private ISysUserService sysUserService;

    public DbRealm(ISysUserService sysUserService){
        this.sysUserService = sysUserService;
    }

    @Override
    public String getName() {
        return "dbRealm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    /**
     * 权限相关信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        return simpleAuthorizationInfo;
    }

    /**
     * 认证相关信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)token;
        String userName = usernamePasswordToken.getUsername();
        SysUserVo sysUser = sysUserService.queryByName(userName);
        if (sysUser == null) {
            throw new AuthenticationException("username is error!");
        }
        return new SimpleAuthenticationInfo(sysUser, sysUser.getPassword(), getName());
    }
}
