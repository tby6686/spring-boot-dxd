/*
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.topnetwork.dxd.framework.exception;

import cn.com.topnetwork.dxd.api.ApiCode;
import cn.com.topnetwork.dxd.api.ApiResult;
import cn.com.topnetwork.dxd.base.exception.BaseException;
import cn.com.topnetwork.dxd.framework.bean.RequestDetail;
import cn.com.topnetwork.dxd.framework.bean.RequestDetailThreadLocal;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tby
 * @date 2018-11-08
 */
@ControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler {

    /////////////////spring 异常处理//////////////////////

    /**
     * 非法参数验证异常(spring异常)
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResult<List<String>> handleMethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        printRequestDetail();
        BindingResult bindingResult = ex.getBindingResult();
        List<String> list = new ArrayList<>();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            list.add(fieldError.getField()+":"+fieldError.getDefaultMessage());
        }
        Collections.sort(list);
        log.error(getApiCodeString(ApiCode.PARAMETER_EXCEPTION) + ":" + JSON.toJSONString(list));
        return ApiResult.result(ApiCode.PARAMETER_EXCEPTION, list);
    }


    /**
     * HTTP解析请求参数异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<?> httpMessageNotReadableException(HttpMessageNotReadableException exception) {
        printRequestDetail();
        printApiCodeException(ApiCode.PARAMETER_PARSE_EXCEPTION, exception);
        return ApiResult.result(ApiCode.PARAMETER_PARSE_EXCEPTION);
    }

    /**
     * HTTP内容类型异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = HttpMediaTypeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<?> httpMediaTypeException(HttpMediaTypeException exception) {
        printRequestDetail();
        printApiCodeException(ApiCode.HTTP_MEDIA_TYPE_EXCEPTION, exception);
        return ApiResult.result(ApiCode.HTTP_MEDIA_TYPE_EXCEPTION);
    }

    /**
     * http 请求方法不支持异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<?> httpRequestMethodNotSupportedExceptionHandler(Exception exception) {
        printRequestDetail();
        printApiCodeException(ApiCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION, exception);
        return ApiResult.result(ApiCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION, exception.getMessage());
    }

    /////////////////////////shiro 相关异常///////////////////////////
    /**
     * 登录授权异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<?> authenticationExceptionHandler(AuthenticationException exception) {
        printRequestDetail();
        printApiCodeException(ApiCode.AUTHENTICATION_EXCEPTION, exception);
        return ApiResult.result(ApiCode.AUTHENTICATION_EXCEPTION);
    }


    /**
     * 未认证异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = UnauthenticatedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<?> unauthenticatedExceptionHandler(UnauthenticatedException exception) {
        printRequestDetail();
        printApiCodeException(ApiCode.UNAUTHENTICATED_EXCEPTION, exception);
        return ApiResult.result(ApiCode.UNAUTHENTICATED_EXCEPTION);
    }


    /**
     * 未授权异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<?> unauthorizedExceptionHandler(UnauthorizedException exception) {
        printRequestDetail();
        printApiCodeException(ApiCode.UNAUTHORIZED_EXCEPTION, exception);
        return ApiResult.result(ApiCode.UNAUTHORIZED_EXCEPTION);
    }


    /**
     * JWT Token解析异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = JWTDecodeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<?> jWTDecodeExceptionHandler(JWTDecodeException exception) {
        printRequestDetail();
        printApiCodeException(ApiCode.JWTDECODE_EXCEPTION, exception);
        return ApiResult.result(ApiCode.JWTDECODE_EXCEPTION);
    }

    ///////////////////////自定义异常处理//////////////////

    /**
     * 自定义业务/数据异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = {BaseException.class})
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Boolean> springBootDxdExceptionHandler(BaseException exception) {
        printRequestDetail();
        log.error("springBootDxdException:", exception);
        return new ApiResult<Boolean>()
                .setCode(exception.getErrorCode())
                .setMessage(exception.getMessage());
    }

    /**
     * 默认的异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<?> exceptionHandler(Exception exception) {
        printRequestDetail();
        printApiCodeException(ApiCode.SYSTEM_EXCEPTION, exception);
        return ApiResult.result(ApiCode.SYSTEM_EXCEPTION);
    }

    /**
     * 打印请求详情
     */
    private void printRequestDetail() {
        RequestDetail requestDetail = RequestDetailThreadLocal.getRequestDetail();
        if (requestDetail != null) {
            log.error("异常来源：ip: {}, path: {}", requestDetail.getIp(), requestDetail.getPath());
        }
    }

    /**
     * 获取ApiCode格式化字符串
     *
     * @param apiCode
     * @return
     */
    private String getApiCodeString(ApiCode apiCode) {
        if (apiCode != null) {
            return String.format("errorCode: %s, errorMessage: %s", apiCode.getCode(), apiCode.getMessage());
        }
        return null;
    }

    /**
     * 打印错误码及异常
     *
     * @param apiCode
     * @param exception
     */
    private void printApiCodeException(ApiCode apiCode, Exception exception) {
        log.error(getApiCodeString(apiCode), exception);
    }

}
