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

package com.dtstack.batch.engine.hdfs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.domain.BatchTask;
import com.dtstack.batch.engine.rdbms.common.HadoopConf;
import com.dtstack.batch.engine.rdbms.common.HdfsOperator;
import com.dtstack.batch.enums.TaskOperateType;
import com.dtstack.batch.service.job.ITaskService;
import com.dtstack.engine.common.enums.EJobType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * hadoop平台上关联任务
 * Date: 2019/5/23
 * Company: www.dtstack.com
 * @author xuchao
 */

@Service
public class BatchHadoopTaskService implements ITaskService {

    private static final Logger LOG = LoggerFactory.getLogger(BatchHadoopTaskService.class);

    @Autowired
    private EnvironmentContext env;

    /**
     * 执行sql或者脚本上传到hdfs
     * @param tenantId
     * @param content
     * @param taskType
     * @param taskName
     * @param tenantId
     * @return
     */
    @Override
    public String uploadSqlText(Long tenantId, String content, Integer taskType, String taskName) {
        String hdfsPath = null;
        try {
            // shell任务，创建脚本文件
            String fileName = null;
            if (taskType.equals(EJobType.SHELL.getVal())) {
                fileName = String.format("shell_%s_%s_%s.sh", tenantId, taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.PYTHON.getVal())) {
                fileName = String.format("python_%s_%s_%s.py", tenantId, taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.SPARK_PYTHON.getVal())) {
                fileName = String.format("pyspark_%s_%s_%s.py", tenantId, taskName, System.currentTimeMillis());
            }

            if (fileName != null) {
                hdfsPath = this.env.getHdfsBatchPath() + fileName;
                if (taskType.equals(EJobType.SHELL.getVal())) {
                    content = content.replaceAll("\r\n", System.getProperty("line.separator"));
                }
                HdfsOperator.uploadInputStreamToHdfs(HadoopConf.getConfiguration(tenantId),HadoopConf.getHadoopKerberosConf(tenantId), content.getBytes(), hdfsPath);
            }
        } catch (final Exception e) {
            BatchHadoopTaskService.LOG.error("", e);
            throw new RdosDefineException("Update task to HDFS failure:" + e.getMessage());
        }

        return HadoopConf.getDefaultFs(tenantId) + hdfsPath;
    }

    @Override
    public void readyForPublishTaskInfo(final BatchTask task, final Long tenantId) {

        if (task.getTaskType().equals(EJobType.SPARK_PYTHON.getVal()) && StringUtils.isNotBlank(task.getExeArgs())) {
            final JSONObject args = JSON.parseObject(task.getExeArgs());
            if (args.getInteger("operateModel").equals(TaskOperateType.EDIT.getType())) {
                final String fileDir = this.uploadSqlText(tenantId, task.getSqlText(), task.getTaskType(), task.getName());
                args.put("hdfsPath", fileDir);
                task.setExeArgs(args.toJSONString());
            }
        }
    }
}