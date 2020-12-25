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

package cn.com.topnetwork.dxd.system.entity;

import cn.com.topnetwork.dxd.base.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <pre>
 * 系统角色
 * </pre>
 *
 * @author tby
 * @since 2019-10-24
 */
@Data
@Table(name="sys_role")
public class SysRoleVo extends BaseEntity {
    private static final long serialVersionUID = -487738234353456553L;

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="code")
    private String code;

    @Column(name="type")
    private Integer type;

    @Column(name="state")
    private Integer state;

    @Column(name="remark")
    private String remark;

    @Column(name="version")
    private Integer version;

    @Column(name="create_time")
    private Date createTime;

    @Column(name="update_time")
    private Date updateTime;

}
