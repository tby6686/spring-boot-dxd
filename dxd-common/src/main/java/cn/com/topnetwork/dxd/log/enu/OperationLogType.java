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

package cn.com.topnetwork.dxd.log.enu;

import cn.com.topnetwork.dxd.base.enu.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作日志类型枚举
 *
 * @author tby
 * @date 2020/3/19
 **/
@Getter
@AllArgsConstructor
public enum OperationLogType implements BaseEnum {
    /**
     * 其它
     **/
    OTHER(0, "其它"),
    /**
     * 添加
     **/
    ADD(1, "添加"),
    /**
     * 修改
     **/
    UPDATE(2, "修改"),
    /**
     * 删除
     **/
    DELETE(3, "删除"),
    /**
     * 查询
     **/
    QUERY_LIST(4, "列表查询"),
    /**
     * 详情查询
     **/
    QUERY_DETAIL(5, "详情查询");

    private Integer code;
    private String desc;

}
