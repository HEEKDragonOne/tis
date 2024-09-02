/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qlangtech.tis.datax;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.qlangtech.tis.plugin.StoreResourceType;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-05-06 15:01
 **/
public class CuratorDataXTaskMessage implements IDataXTaskRelevant {

    public static String serialize(CuratorDataXTaskMessage item) {
        return JSON.toJSONString(item, false);
    }

    public static CuratorDataXTaskMessage deserialize(String json) {
        return JSON.parseObject(json, CuratorDataXTaskMessage.class);
    }

    /**
     * 也可能是WorkFlow名称，不想在类中额外添加属性了，直接使用这个属性就行了
     */
    private String dataXName;

    @JSONField(serialize = false)
    @Override
    public File getSpecifiedLocalLoggerPath() {

        if (StringUtils.isNotEmpty(this.localLoggerPath)) {
            return new File(this.localLoggerPath);
        }
        return null;
    }

    public String getLocalLoggerPath() {
        return localLoggerPath;
    }

    public void setLocalLoggerPath(String localLoggerPath) {
        this.localLoggerPath = localLoggerPath;
    }

    private String localLoggerPath;
    private int taskSerializeNum;

    @Override
    public <T> void setAttr(Class<T> key, Object val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getAttr(Class<T> key) {
        throw new UnsupportedOperationException();
    }

    /**
     * 资源类型
     */
    private StoreResourceType resType;

    private Integer jobId;

    private String jobName;
    // 估计总记录数目
    private Integer allRowsApproximately;

    private long execEpochMilli;

    public long getExecEpochMilli() {
        return this.execEpochMilli;
    }

    @Override
    public String getFormatTime(TimeFormat format) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTaskSerializeNum() {
        return taskSerializeNum;
    }

    public void setTaskSerializeNum(int taskSerializeNum) {
        this.taskSerializeNum = taskSerializeNum;
    }

    // public String getExecTimeStamp() {
//        return execTimeStamp;
//    }

    public void setExecTimeStamp(long epochMilli) {
        this.execEpochMilli = epochMilli;
    }

    public Integer getAllRowsApproximately() {
        return allRowsApproximately;
    }

    public void setAllRowsApproximately(Integer allRowsApproximately) {
        this.allRowsApproximately = allRowsApproximately;
    }

    public String getDataXName() {
        return dataXName;
    }

    @Override
    public Integer getTaskId() {
        return this.jobId;
    }


    public String getJobName() {
        return jobName;
    }

    public StoreResourceType getResType() {
        return resType;
    }

    public void setResType(StoreResourceType resType) {
        this.resType = resType;
    }

    public void setDataXName(String dataXName) {
        this.dataXName = dataXName;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

//    public void setJobPath(String jobPath) {
//        this.jobPath = jobPath;
//    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }


    @Override
    public String toString() {
        return "{" +
                "dataXName='" + dataXName + '\'' +
                ", taskSerializeNum=" + taskSerializeNum +
                ", resType=" + resType +
                ", jobId=" + jobId +
                ", jobName='" + jobName + '\'' +
                ", allRowsApproximately=" + allRowsApproximately +
                ", execEpochMilli=" + execEpochMilli +
                '}';
    }
}
