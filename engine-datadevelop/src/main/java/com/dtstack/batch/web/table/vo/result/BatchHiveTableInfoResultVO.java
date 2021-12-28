/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/2/26 4:11 下午
 */
@Data
@ApiModel("查询hive表信息接口-返回VO")
public class BatchHiveTableInfoResultVO {

    @ApiModelProperty(value = "数据库名称", example = "table_zy_5")
    private String db;

    @ApiModelProperty(value = "所属用户", example = "admin")
    private String owner;

    @ApiModelProperty(value = "建表时间", example = "Thu Feb 25 21:19:02 CST 2021")
    private String createdTime;

    @ApiModelProperty(value = "表最后访问时间", example = "Thu Jan 01 08:00:00 CST 1970")
    private String lastAccess;

    @ApiModelProperty(value = "创建人", example = "admin")
    private String createdBy;

    @ApiModelProperty(value = "表名称", example = "table_01")
    private String name;

    @ApiModelProperty(value = "表描述", example = "这是一张hive表")
    private String comment;

    @ApiModelProperty(value = "分隔符", example = "\u0001")
    private String delim;

    @ApiModelProperty(value = "存储类型", example = "ORC")
    private String storeType;

    @ApiModelProperty(value = "hdfs文件路径", example = "hdfs://hive/warehouse/table_zy_5.db/rdos_batch_apply")
    private String path;

    @ApiModelProperty(value = "内部表：MANAGED 或 外部表：EXTERNAL", example = "MANAGED")
    private String externalOrManaged;

    @ApiModelProperty(value = "是否分区表", example = "false")
    private Boolean partitionTable;

}