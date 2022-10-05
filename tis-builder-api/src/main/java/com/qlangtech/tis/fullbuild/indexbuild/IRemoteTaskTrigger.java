/**
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.qlangtech.tis.fullbuild.indexbuild;

import java.util.Collections;
import java.util.List;

/**
 * @author 百岁（baisui@qlangtech.com）
 * @date 2020/04/13
 */
public interface IRemoteTaskTrigger extends Runnable {
    String KEY_DELTA_STREM_DEBUG = "hudiDeltaStreamDebug";
    String getTaskName();

    default List<String> getTaskDependencies() {
        return Collections.emptyList();
    }

    /**
     * 是否是异步任务
     *
     * @return
     */
    default boolean isAsyn() {
        return false;
    }

    /**
     * 异步任务名称
     *
     * @return
     */
    public default String getAsynJobName() {
        // 只有 isAsyn 返回true时候才能调用该方法
        throw new UnsupportedOperationException();
    }

//    /**
//     * 触发任务
//     */
//    void submitJob();

    /**
     * 终止任务
     */
    default void cancel() {
        throw new UnsupportedOperationException();
    }

  //  RunningStatus getRunningStatus();
}
