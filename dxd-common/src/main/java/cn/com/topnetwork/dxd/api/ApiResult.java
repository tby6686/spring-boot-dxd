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

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * REST API 返回结果
 * </p>
 *
 * @author tby
 * @since 2018-11-08
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
public class ApiResult<T> implements Serializable {
	private static final long serialVersionUID = 8004487252556526569L;

	/**
     * 响应码
     */
    private int code;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间
     */
    /**
     * 响应时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    public ApiResult() {
        time  = new Date();
    }

    public static <T> ApiResult<T> result(ApiCode apiCode,String message,T data){
        boolean success = false;
        if (apiCode.getCode() == ApiCode.SUCCESS.getCode()){
            success = true;
        }
        String apiMessage = apiCode.getMessage();
        if(StringUtils.isEmpty(message)){
            message = apiMessage;
        }
        return (ApiResult<T>) ApiResult.builder()
                .code(apiCode.getCode())
                .message(message)
                .data(data)
                .success(success)
                .time(new Date())
                .build();
    }

    public static ApiResult<?> result(ApiCode apiCode,String message){
        return result(apiCode,message,null);
    }

    public static <T> ApiResult<T> result(ApiCode apiCode,T data){
        return result(apiCode,null,data);
    }

    public static ApiResult<?> result(ApiCode apiCode){
        return result(apiCode,null);
    }


    public static <T> ApiResult<T> success(String message,T data){
        return result(ApiCode.SUCCESS,message,data);
    }

    public static ApiResult<?> success(String message){
        return result(ApiCode.SUCCESS,message);
    }

    public static <T> ApiResult<T> success(T data){
        return result(ApiCode.SUCCESS,data);
    }

    public static  ApiResult success(){
        return result(ApiCode.SUCCESS);
    }

    public static <T> ApiResult<T> fail(String message,T data){
        return result(ApiCode.FAIL,message,data);
    }

    public static ApiResult<?> fail(String message){
        return result(ApiCode.FAIL,message);
    }

    public static <T> ApiResult<T> fail(T data){
        return result(ApiCode.FAIL,data);
    }

    public static  ApiResult fail(){
        return result(ApiCode.FAIL);
    }

}