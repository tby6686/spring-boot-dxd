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

package cn.com.topnetwork.dxd.api;

/**
 * <p>
 * REST API 响应码
 * </p>
 *
 * @author tby
 * @since 2018-11-08
 */
public enum ApiCode {

    /**
     * 操作成功
     **/
    SUCCESS(200, "操作成功"),

    /**
     * 已创建
     **/
    CREATED(201, "已创建"),

    /**
     * 非法访问
     **/
    UNAUTHORIZED(401, "非法访问"),
    /**
     * 拒绝访问
     **/
    FORBIDDEN(403, "拒绝访问"),

    /**
     * 你请求的资源不存在
     **/
    NOT_FOUND(404, "请求资源不存在"),

    /**
     * 操作失败
     **/
    FAIL(500, "操作失败"),

    /**
     * 系统异常
     **/
    SYSTEM_EXCEPTION(5000, "系统异常"),

    /**
     * 请求参数校验异常
     **/
    PARAMETER_EXCEPTION(5001, "请求参数校验异常"),

    /**
     * 请求参数解析异常
     **/
    PARAMETER_PARSE_EXCEPTION(5002, "请求参数解析异常"),

    /**
     * HTTP内容类型异常
     **/
    HTTP_MEDIA_TYPE_EXCEPTION(5003, "HTTP内容类型异常"),

    /**
     * http 请求方法不存在
     */
    HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION(5004, "METHOD NOT SUPPORTED"),

    /**
     * 登录授权异常
     **/
    AUTHENTICATION_EXCEPTION(5100, "登录授权异常"),
    /**
     * 认证不通过
     **/
    UNAUTHENTICATED_EXCEPTION(5101, "认证不通过"),
    /**
     * 没有访问权限
     **/
    UNAUTHORIZED_EXCEPTION(5102, "没有访问权限"),
    /**
     * JWT Token解析异常
     **/
    JWTDECODE_EXCEPTION(5103, "Token解析异常");

   ;

    private final int code;
    private final String message;

    ApiCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static ApiCode getApiCode(int code) {
        ApiCode[] ecs = ApiCode.values();
        for (ApiCode ec : ecs) {
            if (ec.getCode() == code) {
                return ec;
            }
        }
        return SUCCESS;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
