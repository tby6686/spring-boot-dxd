package cn.com.topnetwork.dxd.system.service;

import cn.com.topnetwork.dxd.api.ApiResult;
import cn.com.topnetwork.dxd.system.param.LoginParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ILoginService {

    ApiResult login(LoginParam loginParam, HttpServletRequest request, HttpServletResponse response);


    ApiResult logout(HttpServletRequest request);

}
