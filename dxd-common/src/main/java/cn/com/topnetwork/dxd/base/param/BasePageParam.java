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

package cn.com.topnetwork.dxd.base.param;

import cn.com.topnetwork.dxd.constant.ConstantCommon;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 查询参数
 *
 * @author tby
 * @since 2018-11-08
 */
@Data
@ApiModel("查询参数对象")
public abstract class BasePageParam implements Serializable {
    private static final long serialVersionUID = -3263921252635611410L;

    @ApiModelProperty(value = "页码,默认为1", example = "1")
    private Integer pageIndex = ConstantCommon.DEFAULT_PAGE_INDEX;

    @ApiModelProperty(value = "页大小,默认为20", example = "20")
    private Integer pageSize = ConstantCommon.DEFAULT_PAGE_SIZE;

    @ApiModelProperty("排序集合")
    private HashMap<String,String> pageSorts;

    public void setPageIndex(Integer pageIndex) {
        if (pageIndex == null || pageIndex <= 0) {
            this.pageIndex = ConstantCommon.DEFAULT_PAGE_INDEX;
        } else {
            this.pageIndex = pageIndex;
        }
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            this.pageSize = ConstantCommon.DEFAULT_PAGE_SIZE;
        } else {
            this.pageSize = pageSize;
        }
    }

}
