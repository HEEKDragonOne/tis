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
package com.qlangtech.tis.rpc.server;

import com.qlangtech.tis.exec.ExecutePhaseRange;
import com.qlangtech.tis.fullbuild.phasestatus.JobLog;
import com.qlangtech.tis.fullbuild.phasestatus.PhaseStatusCollection;
import com.qlangtech.tis.fullbuild.phasestatus.impl.BuildSharedPhaseStatus;
import com.qlangtech.tis.fullbuild.phasestatus.impl.DumpPhaseStatus;
import com.qlangtech.tis.fullbuild.phasestatus.impl.JoinPhaseStatus;
import com.qlangtech.tis.grpc.IncrStatusGrpc;
import com.qlangtech.tis.grpc.LaunchReportInfoEntry;
import com.qlangtech.tis.grpc.TableSingleDataIndexStatus;
import com.qlangtech.tis.realtime.yarn.rpc.IncrStatusUmbilicalProtocol;
import com.qlangtech.tis.realtime.yarn.rpc.JobType;
import com.qlangtech.tis.realtime.yarn.rpc.LaunchReportInfo;
import com.qlangtech.tis.realtime.yarn.rpc.MasterJob;
import com.qlangtech.tis.realtime.yarn.rpc.PingResult;
import com.qlangtech.tis.realtime.yarn.rpc.TopicInfo;
import com.qlangtech.tis.realtime.yarn.rpc.UpdateCounterMap;
import com.qlangtech.tis.rpc.grpc.log.LogCollectorClient;
import com.qlangtech.tis.rpc.grpc.log.common.Empty;
import com.qlangtech.tis.rpc.grpc.log.common.JoinTaskStatus;
import com.qlangtech.tis.rpc.grpc.log.common.TableDumpStatus;
import com.qlangtech.tis.rpc.grpc.log.stream.LogCollectorGrpc;
import com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam;
import com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author 百岁（baisui@qlangtech.com）
 * @create: 2020-05-01 11:50
 */
public class IncrStatusClient implements IncrStatusUmbilicalProtocol {

    private static final Logger logger = LoggerFactory.getLogger(IncrStatusClient.class);

    private final IncrStatusGrpc.IncrStatusBlockingStub blockingStub;

    private final IncrStatusGrpc.IncrStatusStub asyncStub;
    private final LogCollectorGrpc.LogCollectorBlockingStub logCollectorBlockingStub;

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public IncrStatusClient(Channel channel) {
        blockingStub = IncrStatusGrpc.newBlockingStub(channel);
        asyncStub = IncrStatusGrpc.newStub(channel);
        this.logCollectorBlockingStub = LogCollectorGrpc.newBlockingStub(channel);
    }

    // PingResult ping = blockingStub.ping(Empty.newBuilder().build());
    // System.out.println(ping.getValue());
    @Override
    public PingResult ping() {
        com.qlangtech.tis.grpc.PingResult ping = blockingStub.ping(Empty.newBuilder().build());
        PingResult result = new PingResult();
        result.setValue(ping.getValue());
        return result;
    }

    @Override
    public MasterJob reportStatus(UpdateCounterMap upateCounter) {
        com.qlangtech.tis.grpc.UpdateCounterMap.Builder builder = com.qlangtech.tis.grpc.UpdateCounterMap.newBuilder();
        upateCounter.getData().entrySet().forEach((e) -> {
            TableSingleDataIndexStatus.Builder sbuilder = TableSingleDataIndexStatus.newBuilder();
            com.qlangtech.tis.realtime.transfer.TableSingleDataIndexStatus value = e.getValue();
            value.getTableConsumeData().entrySet().forEach((ce) -> {
                sbuilder.putTableConsumeData(ce.getKey(), ce.getValue());
            });
            sbuilder.setBufferQueueRemainingCapacity(value.getBufferQueueRemainingCapacity());
            sbuilder.setBufferQueueUsedSize(value.getBufferQueueUsedSize());
            sbuilder.setConsumeErrorCount(value.getConsumeErrorCount());
            sbuilder.setIgnoreRowsCount(value.getIgnoreRowsCount());
            sbuilder.setTis30SAvgRT(value.getTis30sAvgRT());
            sbuilder.setUuid(value.getUUID());
            sbuilder.setIncrProcessPaused(value.isIncrProcessPaused());
            com.qlangtech.tis.grpc.TableSingleDataIndexStatus s = sbuilder.build();
            builder.putData(e.getKey().getKey(), s);
        });
        builder.setFrom(upateCounter.getFrom());
        builder.setGcCounter(upateCounter.getGcCounter());
        builder.setUpdateTime(upateCounter.getUpdateTime());
        com.qlangtech.tis.grpc.UpdateCounterMap updateCounterMap = builder.build();
        com.qlangtech.tis.grpc.MasterJob masterJob = blockingStub.reportStatus(updateCounterMap);
        if (masterJob.getJobType() == com.qlangtech.tis.grpc.MasterJob.JobType.None) {
            return null;
        }
        // JobType jobType, String indexName, String uuid
        MasterJob job = new MasterJob(JobType.parseJobType(masterJob.getJobTypeValue()), masterJob.getIndexName(), masterJob.getUuid());
        job.setStop(masterJob.getStop());
        job.setCreateTime(masterJob.getCreateTime());
        return job;
    }

    @Override
    public void nodeLaunchReport(LaunchReportInfo launchReportInfo) {
        com.qlangtech.tis.grpc.LaunchReportInfo.Builder builder = com.qlangtech.tis.grpc.LaunchReportInfo.newBuilder();
        launchReportInfo.getCollectionFocusTopicInfo().entrySet().forEach((e) -> {
            com.qlangtech.tis.grpc.TopicInfo.Builder topicinfoBuilder = com.qlangtech.tis.grpc.TopicInfo.newBuilder();
            TopicInfo tinfo = e.getValue();
            tinfo.getTopicWithTags().entrySet().forEach((i) -> {
                LaunchReportInfoEntry.Builder ebuilder = LaunchReportInfoEntry.newBuilder();
                ebuilder.setTopicName(i.getKey());
                ebuilder.addAllTagName(i.getValue());
                topicinfoBuilder.addTopicWithTags(ebuilder.build());
            });
            builder.putCollectionFocusTopicInfo(e.getKey(), /*collection name*/
                    topicinfoBuilder.build());
        });
        blockingStub.nodeLaunchReport(builder.build());
    }

    @Override
    public void reportDumpTableStatus(DumpPhaseStatus.TableDumpStatus tableDumpStatus) {
//        TableDumpStatus.Builder builder = TableDumpStatus.newBuilder();
//        builder.setAllRows(tableDumpStatus.getAllRows());
//        builder.setComplete(tableDumpStatus.isComplete());
//        builder.setFaild(tableDumpStatus.isFaild());
//        builder.setReadRows(tableDumpStatus.getReadRows());
//        builder.setTableName(tableDumpStatus.getName());
//        builder.setTaskid(tableDumpStatus.getTaskid());
//        builder.setWaiting(tableDumpStatus.isWaiting());
        blockingStub.reportDumpTableStatus(convert(tableDumpStatus));
    }

    public static com.qlangtech.tis.rpc.grpc.log.common.TableDumpStatus convert(DumpPhaseStatus.TableDumpStatus tableDumpStatus) {
        TableDumpStatus.Builder builder = TableDumpStatus.newBuilder();
        builder.setAllRows(tableDumpStatus.getAllRows());
        builder.setComplete(tableDumpStatus.isComplete());
        builder.setFaild(tableDumpStatus.isFaild());
        builder.setReadRows(tableDumpStatus.getReadRows());
        builder.setTableName(tableDumpStatus.getName());
        builder.setTaskid(tableDumpStatus.getTaskid());
        builder.setWaiting(tableDumpStatus.isWaiting());
        return builder.build();
    }


    @Override
    public void initSynJob(PhaseStatusCollection buildStatus) {

        logCollectorBlockingStub.initTask(LogCollectorClient.convertPP(buildStatus));
    }

    @Override
    public PhaseStatusCollection loadPhaseStatusFromLatest(Integer taskId) {

        Objects.requireNonNull(taskId, "taskId can not be null");
        PPhaseStatusCollection statusCollection = logCollectorBlockingStub
                .loadPhaseStatus(PBuildPhaseStatusParam.newBuilder().setTaskid(taskId).build());
        if (statusCollection == null || statusCollection.getTaskId() < 1) {
            return null;
        }
        return LogCollectorClient.convert(statusCollection, ExecutePhaseRange.fullRange());
    }
//    @Override
//    public PhaseStatusCollection loadPhaseStatusFromLatest(Integer taskId) {
//
//        Builder builder = PSynResTarget.newBuilder();
//        builder.setPipeline(resTarget.isPipeline());
//        builder.setName(resTarget.getName());
//        PPhaseStatusCollection statusCollection = logCollectorBlockingStub.loadPhaseStatusFromLatest(builder.build());
//        if (statusCollection == null || statusCollection.getTaskId() < 1) {
//            return null;
//        }
//        return LogCollectorClient.convert(statusCollection, ExecutePhaseRange.fullRange());
//    }

    @Override
    public void reportBuildIndexStatus(BuildSharedPhaseStatus buildStatus) {
        com.qlangtech.tis.rpc.grpc.log.common.BuildSharedPhaseStatus.Builder
                builder = com.qlangtech.tis.rpc.grpc.log.common.BuildSharedPhaseStatus.newBuilder();
        builder.setAllBuildSize(buildStatus.getAllBuildSize());
        builder.setBuildReaded(buildStatus.getBuildReaded());
        builder.setComplete(buildStatus.isComplete());
        builder.setFaild(buildStatus.isFaild());
        builder.setSharedName(buildStatus.getSharedName());
        builder.setTaskid(buildStatus.getTaskid());
        builder.setWaiting(buildStatus.isWaiting());
        blockingStub.reportBuildIndexStatus(builder.build());

    }


    @Override
    public void reportJoinStatus(Integer taskId, JoinPhaseStatus.JoinTaskStatus joinTaskStatus) {
        //  blockingStub.re
        Objects.requireNonNull(taskId, "taskId can not be null");
        JoinTaskStatus.Builder joinStatBuilder = JoinTaskStatus.newBuilder();
        joinStatBuilder.setTaskid(taskId);
        joinStatBuilder.setJoinTaskName(joinTaskStatus.getName());
        joinStatBuilder.setWaiting(joinTaskStatus.isWaiting());
        joinStatBuilder.setFaild(joinTaskStatus.isFaild());
        joinStatBuilder.setComplete(joinTaskStatus.isComplete());
        com.qlangtech.tis.rpc.grpc.log.common.JobLog.Builder logBuilder = null;
        JobLog log = null;
        for (Map.Entry<Integer, JobLog> entry : joinTaskStatus.jobsStatus.entrySet()) {
            log = entry.getValue();
            logBuilder = com.qlangtech.tis.rpc.grpc.log.common.JobLog.newBuilder();
            logBuilder.setMapper(log.getMapper());
            logBuilder.setReducer(log.getReducer());
            logBuilder.setWaiting(log.isWaiting());
            joinStatBuilder.putJobStatus(entry.getKey(), logBuilder.build());
        }
        blockingStub.reportJoinStatus(joinStatBuilder.build());
    }

    /**
     * Issues several different requests and then exits.
     */
    public static void main(String[] args) throws InterruptedException {
        String target = "localhost:8980";
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [target]");
                System.err.println("");
                System.err.println("  target  The server to connect to. Defaults to " + target);
                System.exit(1);
            }
            target = args[0];
        }
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        try {
            IncrStatusClient client = new IncrStatusClient(channel);
            // Looking for a valid feature
            while (true) {
                client.ping();
                Thread.sleep(1000);
            }
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
