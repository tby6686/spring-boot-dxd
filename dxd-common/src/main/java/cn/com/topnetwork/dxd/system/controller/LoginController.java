package cn.com.topnetwork.dxd.system.controller;

import cn.com.topnetwork.dxd.api.ApiResult;
import cn.com.topnetwork.dxd.base.controller.BaseController;
import cn.com.topnetwork.dxd.log.annotation.Module;
import cn.com.topnetwork.dxd.system.param.LoginParam;
import cn.com.topnetwork.dxd.system.service.ILoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录控制器
 *
 * @author tby
 * @date 2019-09-28
 * @since 1.3.0.RELEASE
 **/
@Slf4j
@RestController
@Module("system")
@Api(value = "系统登录API", tags = {"系统登录"})
public class LoginController extends BaseController {

    @Autowired
    private ILoginService loginService;

    @PostMapping("/login")
    @ApiOperation(value = "登陆操作", notes = "登陆验证并且获取token", response = ApiResult.class)
    public ApiResult login(@Validated @RequestBody LoginParam loginParam, HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = loginService.login(loginParam,request,response);
        return apiResult;
    }

    @PostMapping("/logout")
    @ApiOperation(value = "登出操作", notes = "退出登陆", response = ApiResult.class)
    public ApiResult login(HttpServletRequest request) {
        ApiResult apiResult = loginService.logout(request);
        return apiResult;
    }

}
