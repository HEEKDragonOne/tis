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

package com.qlangtech.tis.order.center;

import com.alibaba.citrus.turbine.Context;
import com.qlangtech.tis.assemble.ExecResult;
import com.qlangtech.tis.assemble.FullbuildPhase;
import com.qlangtech.tis.build.task.IBuildHistory;
import com.qlangtech.tis.coredefine.module.action.TriggerBuildResult;
import com.qlangtech.tis.dao.ICommonDAOContext;
import com.qlangtech.tis.datax.CuratorDataXTaskMessage;
import com.qlangtech.tis.datax.DataXJobInfo;
import com.qlangtech.tis.datax.DataXJobSubmit;
import com.qlangtech.tis.datax.IDataXBatchPost;
import com.qlangtech.tis.datax.IDataxProcessor;
import com.qlangtech.tis.datax.impl.DataxProcessor;
import com.qlangtech.tis.exec.ExecutePhaseRange;
import com.qlangtech.tis.exec.ExecuteResult;
import com.qlangtech.tis.exec.IExecChainContext;
import com.qlangtech.tis.exec.impl.DefaultChainContext;
import com.qlangtech.tis.fullbuild.IFullBuildContext;
import com.qlangtech.tis.fullbuild.indexbuild.IRemoteTaskTrigger;
import com.qlangtech.tis.fullbuild.taskflow.TestParamContext;
import com.qlangtech.tis.job.common.JobCommon;
import com.qlangtech.tis.runtime.module.misc.IControlMsgHandler;
import com.qlangtech.tis.test.TISTestCase;
import com.qlangtech.tis.workflow.pojo.IWorkflow;
import com.qlangtech.tis.workflow.pojo.WorkFlowBuildHistory;
import com.tis.hadoop.rpc.RpcServiceReference;
import junit.framework.Assert;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;

import java.util.List;
import java.util.Optional;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-05-03 20:55
 **/
public class TestIndexSwapTaskflowLauncherWithDataXTrigger extends TISTestCase {

    private static final String DATAX_NAME = "baisuitestTestcase";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        DataXJobSubmit.mockGetter = () -> new MockDataXJobSubmit(null);
    }

    @After
    public void tearDown() throws Exception {
//        super.tearDown();
        DataXJobSubmit.mockGetter = null;
    }

    public void testDataXProcessTrigger() throws Exception {
        IndexSwapTaskflowLauncher taskflowLauncher = new IndexSwapTaskflowLauncher();
        DefaultChainContext chainContext = createRangeChainContext(FullbuildPhase.FullDump, FullbuildPhase.FullDump);
        ExecuteResult executeResult = taskflowLauncher.startWork(chainContext);
        assertTrue(executeResult.isSuccess());
    }

    static final int TASK_ID = 253;

    public static DefaultChainContext createRangeChainContext(FullbuildPhase start, FullbuildPhase end) throws Exception {
        TestParamContext params = new TestParamContext();

        params.set(IFullBuildContext.KEY_APP_NAME, DATAX_NAME);

        params.set(IExecChainContext.COMPONENT_START, start.getName());
        params.set(IExecChainContext.COMPONENT_END, end.getName());
        final DefaultChainContext chainContext = new DefaultChainContext(params);

        ExecutePhaseRange range = chainContext.getExecutePhaseRange();
        Assert.assertEquals(start, range.getStart());
        Assert.assertEquals(end, range.getEnd());

        chainContext.setAttribute(JobCommon.KEY_TASK_ID, TASK_ID);

        chainContext.setMdcParamContext(() -> {
        });
        return chainContext;
    }

    public static class MockDataXJobSubmit extends DataXJobSubmit {

        private final IRemoteTaskTrigger jobTrigger;

        public MockDataXJobSubmit(IRemoteTaskTrigger jobTrigger) {
            this.jobTrigger = jobTrigger;
        }

        @Override
        public InstanceType getType() {
            return InstanceType.LOCAL;
        }

        @Override
        public TriggerBuildResult triggerWorkflowJob(IControlMsgHandler module, Context context
                , IWorkflow workflow, Boolean dryRun, Optional<Long> powerJobWorkflowInstanceIdOpt, Optional<WorkFlowBuildHistory> latestSuccessWorkflowHistory) {
            throw new UnsupportedOperationException();
        }


        @Override
        public boolean cancelTask(IControlMsgHandler module, Context context, IBuildHistory buildHistory) {
            throw new UnsupportedOperationException();
        }

//        @Override
//        public <T> T saveJob(IControlMsgHandler module, Context context, DataxProcessor dataxProcessor) {
//             throw new UnsupportedOperationException();
//        }

        @Override
        public TriggerBuildResult triggerJob(IControlMsgHandler module, Context context, String appName, Optional<Long> workflowInstanceIdOpt) {
            return null;
        }

        @Override
        public IRemoteTaskTrigger createDataXJob(IDataXJobContext taskContext, RpcServiceReference statusRpc
                , DataXJobInfo jobName, IDataxProcessor dataxProcessor, CuratorDataXTaskMessage dataXJobDTO) {


            return jobTrigger;
//            return new IRemoteJobTrigger() {
//                @Override
//                public void submitJob() {
//
//                }
//
//                @Override
//                public RunningStatus getRunningStatus() {
//                    return new RunningStatus(1, true, true);
//                }
//            };
        }

        @Override
        public IDataXJobContext createJobContext(final IExecChainContext parentContext) {
            return IDataXJobContext.create(parentContext);
//                    new IDataXJobContext() {
////                @Override
////                IJoinTaskContext getTaskContext();
//
//                @Override
//                public IJoinTaskContext getTaskContext() {
//                    return parentContext;
//                }
//
//                @Override
//                public void destroy() {
//
//                }
//            };
        }


    }

}
