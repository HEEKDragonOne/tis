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

package com.qlangtech.tis.exec.datax;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qlangtech.tis.assemble.FullbuildPhase;
import com.qlangtech.tis.datax.*;
import com.qlangtech.tis.datax.impl.DataXCfgGenerator;
import com.qlangtech.tis.exec.ExecuteResult;
import com.qlangtech.tis.exec.IExecChainContext;
import com.qlangtech.tis.exec.impl.TrackableExecuteInterceptor;
import com.qlangtech.tis.fullbuild.indexbuild.IRemoteTaskTrigger;
import com.qlangtech.tis.fullbuild.indexbuild.RemoteTaskTriggers;
import com.qlangtech.tis.fullbuild.taskflow.TISReactor;
import com.qlangtech.tis.fullbuild.taskflow.TaskAndMilestone;
import com.qlangtech.tis.manage.common.DagTaskUtils;
import com.qlangtech.tis.manage.impl.DataFlowAppSource;
import com.qlangtech.tis.plugin.ds.ISelectedTab;
import com.qlangtech.tis.rpc.server.IncrStatusUmbilicalProtocolImpl;
import com.qlangtech.tis.sql.parser.DAGSessionSpec;
import com.tis.hadoop.rpc.RpcServiceReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jvnet.hudson.reactor.ReactorListener;
import org.jvnet.hudson.reactor.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * DataX 执行器
 *
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-04-27 15:42
 **/
public class DataXExecuteInterceptor extends TrackableExecuteInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(DataXExecuteInterceptor.class);


    /**
     * 触发一个逻辑表相关子任务
     *
     * @param execChainContext
     * @param appSource
     * @param submit
     * @param statusRpc
     * @param entry
     * @return
     */
    public static RemoteTaskTriggers buildTaskTriggers(IExecChainContext execChainContext, IDataxProcessor appSource
            , DataXJobSubmit submit
            , RpcServiceReference statusRpc //
            , ISelectedTab entry, String dumpTaskId, DAGSessionSpec dagSessionSpec) {
        if (StringUtils.isEmpty(dumpTaskId)) {
            throw new IllegalArgumentException("param dumpTaskId can not be null");
        }
        RemoteTaskTriggers triggers = new RemoteTaskTriggers();
        IRemoteTaskTrigger jobTrigger = null;
        IDataxWriter writer = appSource.getWriter(null, true);
        DataXCfgGenerator.GenerateCfgs cfgFileNames = appSource.getDataxCfgFileNames(null);
        if (CollectionUtils.isEmpty(cfgFileNames.getDataXCfgFiles())) {
            throw new IllegalStateException("dataX cfgFileNames can not be empty");
        }
        DAGSessionSpec postSpec = null;
        DAGSessionSpec dumpSpec = dagSessionSpec.getDpt(dumpTaskId).setMilestone();
        IRemoteTaskTrigger preExec = null;
        if (writer instanceof IDataXBatchPost) {

            IDataXBatchPost batchPostTask = (IDataXBatchPost) writer;

            IRemoteTaskTrigger postTaskTrigger = batchPostTask.createPostTask(execChainContext, entry, cfgFileNames);
            Objects.requireNonNull(postTaskTrigger, "postTaskTrigger can not be null");
            triggers.addJoinPhaseTask(postTaskTrigger);
            postSpec = dumpSpec.getDpt(postTaskTrigger.getTaskName());


            preExec = batchPostTask.createPreExecuteTask(execChainContext, entry);
            if (preExec != null) {
                dagSessionSpec.getDpt(preExec.getTaskName());
                triggers.addDumpPhaseTask(preExec);
            }
        }


        List<String> dptTasks = Lists.newArrayList();
        if (preExec != null) {
            dptTasks.add(preExec.getTaskName());
        }
        List<DataXCfgGenerator.DBDataXChildTask> dataXCfgsOfTab = cfgFileNames.getDataXTaskDependencies(entry.getName());

//        if (taskMap.get(IDataXBatchPost.getPreExecuteTaskName(entry)) != null) {
//            // 说明有前置任务
//            dptTasks = Collections.singletonList(IDataXBatchPost.getPreExecuteTaskName(entry));
//        }

        final DataXJobSubmit.IDataXJobContext dataXJobContext = submit.createJobContext(execChainContext);
        Objects.requireNonNull(dataXJobContext, "dataXJobContext can not be null");

        for (DataXCfgGenerator.DBDataXChildTask fileName : dataXCfgsOfTab) {


            jobTrigger = createDataXJob(dataXJobContext, submit
                    , statusRpc, appSource
                    , new DataXJobSubmit.TableDataXEntity(fileName, entry), dptTasks);
            DAGSessionSpec childDumpSpec = getDumpSpec(postSpec, dumpSpec).getDpt(jobTrigger.getTaskName());

            for (String dpt : jobTrigger.getTaskDependencies()) {
                childDumpSpec.getDpt(dpt);
            }
            // postSpec.getDpt(jobTrigger.getTaskName());

            triggers.addDumpPhaseTask(jobTrigger);
        }
        return triggers;
    }

    private static DAGSessionSpec getDumpSpec(DAGSessionSpec postSpec, DAGSessionSpec dumpSpec) {
        if (postSpec != null) {
            return postSpec;
        }
        if (dumpSpec != null) {
            return dumpSpec;
        }
        throw new IllegalStateException("neither postSpec nor dumpSpec can be null");
    }

    @Override
    protected ExecuteResult execute(IExecChainContext execChainContext) throws Exception {


        // final Map<String, TaskAndMilestone> taskMap = Maps.newHashMap();
        RpcServiceReference statusRpc = getDataXExecReporter();

        IDataxProcessor appSource = execChainContext.getProcessor();
        //  IRemoteTaskTrigger jobTrigger = null;
        // RunningStatus runningStatus = null;


        IDataxReader reader = appSource.getReader(null);

        List<ISelectedTab> selectedTabs = reader.getSelectedTabs();


        DataXJobSubmit.InstanceType expectDataXJobSumit = getDataXTriggerType();
        Optional<DataXJobSubmit> jobSubmit = DataXJobSubmit.getDataXJobSubmit(expectDataXJobSumit);
        // 如果分布式worker ready的话
        if (!jobSubmit.isPresent()) {
            throw new IllegalStateException("can not find expect jobSubmit by type:" + expectDataXJobSumit);
        }

        DataXJobSubmit submit = jobSubmit.get();

        RemoteTaskTriggers tskTriggers = new RemoteTaskTriggers();
        DAGSessionSpec sessionSpec = new DAGSessionSpec();
        for (ISelectedTab entry : selectedTabs) {
            tskTriggers.merge(buildTaskTriggers(execChainContext, appSource, submit, statusRpc, entry, entry.getName(), sessionSpec));
        }
        List<IRemoteTaskTrigger> triggers = DagTaskUtils.createTasks(execChainContext, this, sessionSpec, tskTriggers);

        final DataXJobSubmit.IDataXJobContext dataXJobContext = submit.createJobContext(execChainContext);
        Objects.requireNonNull(dataXJobContext, "dataXJobContext can not be null");
        final ExecutorService executorService = DataFlowAppSource.createExecutorService(execChainContext);
        try {
            // example: "->a ->b a,b->c"
            String dagSessionSpec = triggers.stream().map((trigger) -> {
                List<String> dpts = Objects.requireNonNull(trigger.getTaskDependencies()
                        , "trigger:" + trigger.getTaskName() + " relevant task dependencies can not be null");
                return dpts.stream().collect(Collectors.joining(",")) + "->" + trigger.getTaskName();
            }).collect(Collectors.joining(" "));
            logger.info("dataX:{} of dagSessionSpec:{}", execChainContext.getIndexName(), dagSessionSpec);


            ExecuteResult[] faildResult = new ExecuteResult[]{ExecuteResult.createSuccess()};


            this.executeDAG(executorService, execChainContext, dagSessionSpec, sessionSpec.getTaskMap(), new ReactorListener() {

                @Override
                public void onTaskCompleted(Task t) {
                    // dumpPhaseStatus.isComplete();
                    // joinPhaseStatus.isComplete();
                }

                @Override
                public void onTaskFailed(Task t, Throwable err, boolean fatal) {
                    logger.error(t.getDisplayName(), err);
                    faildResult[0] = ExecuteResult.createFaild().setMessage("status.runningStatus.isComplete():" + err.getMessage());
                    if (err instanceof InterruptedException) {
                        logger.warn("DataX Name:{},taskid:{} has been canceled"
                                , execChainContext.getIndexName(), execChainContext.getTaskId());
                        // this job has been cancel, trigger from TisServlet.doDelete()
                        for (IRemoteTaskTrigger tt : triggers) {
                            try {
                                tt.cancel();
                            } catch (Throwable ex) {
                            }
                        }
                    }
                }
            });

            for (IRemoteTaskTrigger trigger : triggers) {
                if (trigger.isAsyn()) {
                    execChainContext.addAsynSubJob(new IExecChainContext.AsynSubJob(trigger.getAsynJobName()));
                }
            }
            return faildResult[0];
        } finally {
            try {
                dataXJobContext.destroy();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
            executorService.shutdown();
        }
    }


    private void executeDAG(ExecutorService executorService, IExecChainContext execChainContext, String dagSessionSpec
            , Map<String, TaskAndMilestone> taskMap, ReactorListener reactorListener) {
        try {
            TISReactor reactor = new TISReactor(execChainContext, taskMap);
            // String dagSessionSpec = topology.getDAGSessionSpec();
            logger.info("dagSessionSpec:" + dagSessionSpec);

            //  final PrintWriter w = new PrintWriter(sw, true);
            ReactorListener listener = new ReactorListener() {
                // TODO: Does it really needs handlers to be synchronized?
//                @Override
//                public synchronized void onTaskStarted(Task t) {
//            //        w.println("Started " + t.getDisplayName());
//                }

                @Override
                public synchronized void onTaskCompleted(Task t) {
                    //   w.println("Ended " + t.getDisplayName());
//                    processTaskResult(execChainContext, (TISReactor.TaskImpl) t, new ITaskResultProcessor() {
//                        @Override
//                        public void process(DumpPhaseStatus dumpPhase, TISReactor.TaskImpl task) {
//                        }
//
//                        @Override
//                        public void process(JoinPhaseStatus joinPhase, TISReactor.TaskImpl task) {
//                        }
//                    });
                }

                @Override
                public synchronized void onTaskFailed(Task t, Throwable err, boolean fatal) {
                    // w.println("Failed " + t.getDisplayName() + " with " + err);
                }
//
//                @Override
//                public synchronized void onAttained(Milestone milestone) {
//                    w.println("Attained " + milestone);
//                }
            };


            // 执行DAG地调度
            //executorService
            reactor.execute(executorService, reactor.buildSession(dagSessionSpec), listener, reactorListener);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected static IRemoteTaskTrigger createDataXJob(
            DataXJobSubmit.IDataXJobContext execChainContext
            , DataXJobSubmit submit
            , RpcServiceReference statusRpc
            , IDataxProcessor appSource, DataXJobSubmit.TableDataXEntity fileName, List<String> dependencyTasks) {

        if (submit.getType() == DataXJobSubmit.InstanceType.DISTRIBUTE) {
            IncrStatusUmbilicalProtocolImpl statCollect = IncrStatusUmbilicalProtocolImpl.getInstance();
            // 将指标纬度统计向注册到内存中，下一步可提供给DataX终止功能使用
            statCollect.getAppSubExecNodeMetrixStatus(execChainContext.getTaskContext().getIndexName(), fileName.getFileName());
        }
        return submit.createDataXJob(
                execChainContext, statusRpc, appSource, fileName, dependencyTasks);
    }


    private DataXJobSubmit.InstanceType getDataXTriggerType() {
//        DataXJobWorker jobWorker = DataXJobWorker.getJobWorker(DataXJobWorker.K8S_DATAX_INSTANCE_NAME);
//        boolean dataXWorkerServiceOnDuty = jobWorker != null && jobWorker.inService();//.isDataXWorkerServiceOnDuty();
//        return dataXWorkerServiceOnDuty ? DataXJobSubmit.InstanceType.DISTRIBUTE : DataXJobSubmit.InstanceType.LOCAL;
        return DataXJobSubmit.getDataXTriggerType();
    }


    @Override
    public Set<FullbuildPhase> getPhase() {
        return Sets.newHashSet(FullbuildPhase.FullDump, FullbuildPhase.JOIN);
    }

}
