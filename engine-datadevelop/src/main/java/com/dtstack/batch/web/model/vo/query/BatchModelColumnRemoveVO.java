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

package com.dtstack.batch.web.model.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("移除字段模型信息")
public class BatchModelColumnRemoveVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "字段模型ID列表", required = true)
    private List<Long> ids;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long userId;
}