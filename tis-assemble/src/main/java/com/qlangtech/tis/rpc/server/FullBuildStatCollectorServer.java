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

import ch.qos.logback.classic.spi.LoggingEvent;
import com.google.common.collect.Lists;
import com.qlangtech.tis.assemble.FullbuildPhase;
import com.qlangtech.tis.exec.ExecutePhaseRange;
import com.qlangtech.tis.exec.impl.TrackableExecuteInterceptor;
import com.qlangtech.tis.fullbuild.phasestatus.PhaseStatusCollection;
import com.qlangtech.tis.log.RealtimeLoggerCollectorAppender;
import com.qlangtech.tis.order.center.IndexSwapTaskflowLauncher;
import com.qlangtech.tis.rpc.grpc.log.LogCollectorClient;
import com.qlangtech.tis.rpc.grpc.log.stream.LogCollectorGrpc;
import com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam;
import com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState;
import com.qlangtech.tis.rpc.grpc.log.stream.PMonotorTarget;
import com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection;
import com.qlangtech.tis.trigger.jst.MonotorTarget;
import com.qlangtech.tis.trigger.socket.LogType;
import com.qlangtech.tis.utils.Utils;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 全量执行各个节点状态收集，供console端实时拉取
 *
 * @author 百岁（baisui@qlangtech.com）
 * @create: 2020-05-11 10:17
 */
public class FullBuildStatCollectorServer extends LogCollectorGrpc.LogCollectorImplBase {

    private static final Logger logger = LoggerFactory.getLogger(FullBuildStatCollectorServer.class);

    private static final FullBuildStatCollectorServer instance = new FullBuildStatCollectorServer();

    public static FullBuildStatCollectorServer getInstance() {
        return instance;
    }

    private FullBuildStatCollectorServer() {
    }

    public static void main(String[] args) throws Exception {
        FullBuildStatCollectorServer svc = new FullBuildStatCollectorServer();
        final Server server = ServerBuilder.forPort(9999).addService(svc).build().start();
        logger.info("Listening on " + server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("Shutting down");
                try {
                    server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        });
        server.awaitTermination();
    }

    public static RegisterMonitorEventHook registerMonitorEventHook = new RegisterMonitorEventHook();

    @Override
    public void loadPhaseStatus(PBuildPhaseStatusParam request, StreamObserver<PPhaseStatusCollection> responseObserver) {
        PhaseStatusCollection statusCollection = IndexSwapTaskflowLauncher.loadPhaseStatusFromLocal((int) request.getTaskid());
        logger.info("taskId:{} load relevant status from local persist is null:{}", request.getTaskid(), statusCollection != null);
        responseObserver.onNext((statusCollection != null) ? LogCollectorClient.convertPP(statusCollection) : null);
        responseObserver.onCompleted();
    }


    /**
     * 监听执行日志，详细信息
     *
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<PMonotorTarget> registerMonitorEvent(StreamObserver<PExecuteState> responseObserver) {
        registerMonitorEventHook.startSession();
        final ServerCallStreamObserver<PExecuteState> serverCallStreamObserver = (ServerCallStreamObserver<PExecuteState>) responseObserver;
        RealtimeLoggerCollectorAppender.LoggerCollectorAppenderListener listener = new RealtimeLoggerCollectorAppender.LoggerCollectorAppenderListener() {

            @Override
            public void readLogTailer(RealtimeLoggerCollectorAppender.LoggingEventMeta meta, File logFile) {
                Utils.readLastNLine(logFile, 300, (line) -> {
                    if (line == null) {
                        return;
                    }
                    PExecuteState s = createLogState(meta, 0, null, line);
                    responseObserver.onNext(s);
                    registerMonitorEventHook.send2ClientFromFileTailer(logFile, s);
                });
            }

            @Override
            public void process(RealtimeLoggerCollectorAppender.LoggingEventMeta mtarget, LoggingEvent e) {
                try {
                    String msg = e.getFormattedMessage();
                    if (msg == null) {
                        return;
                    }
                    try (BufferedReader msgReader = new BufferedReader(new StringReader(e.getFormattedMessage()))) {
                        String line = null;
                        while ((line = msgReader.readLine()) != null) {
                            PExecuteState s = createLogState(mtarget, e.getTimeStamp(), LogCollectorClient.convert(e.getLevel()), line);
                            responseObserver.onNext(s);
                            registerMonitorEventHook.send2Client(s, e);
                        }
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }


            }

            private PExecuteState createLogState(RealtimeLoggerCollectorAppender.LoggingEventMeta mtarget
                    , long timestamp, com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState.InfoType level, String msg) {
                PExecuteState.Builder serverLog = PExecuteState.newBuilder();
                serverLog.setTime(timestamp);
                if (level != null) {
                    serverLog.setInfoType(level);
                }
                serverLog.setMsg(msg);
                serverLog.setLogType(LogCollectorClient.convert(mtarget.logTypeVal));
                if (mtarget.taskid != null) {
                    serverLog.setTaskId(mtarget.taskid);
                }
                return serverLog.build();
            }

            @Override
            public boolean isClosed() {
                return !serverCallStreamObserver.isReady();
            }
        };
        class OnReadyHandler implements Runnable {

            // Guard against spurious onReady() calls caused by a race between onNext() and onReady(). If the transport
            // toggles isReady() from false to true while onNext() is executing, but before onNext() checks isReady(),
            // request(1) would be called twice - once by onNext() and once by the onReady() scheduled during onNext()'s
            // execution.
            private boolean wasReady = false;

            @Override
            public void run() {
                if (serverCallStreamObserver.isReady() && !wasReady) {
                    wasReady = true;
                    logger.info("READY");
                    // Signal the request sender to send one message. This happens when isReady() turns true, signaling that
                    // the receive buffer has enough free space to receive more messages. Calling request() serves to prime
                    // the message pump.
                    // serverCallStreamObserver.request(1);
                }
            }
        }
        final OnReadyHandler onReadyHandler = new OnReadyHandler();
        serverCallStreamObserver.setOnReadyHandler(onReadyHandler);
        // Give gRPC a StreamObserver that can observe and process incoming requests.
        return new StreamObserver<PMonotorTarget>() {

            @Override
            public void onNext(PMonotorTarget request) {
                // Process the request and send a response or an error.
                try {
                    // Accept and enqueue the request.
                    // String name = request.getCollection();
                    MonotorTarget mtarget = LogCollectorClient.convert(request);
                    // System.out.println("==========================receive mtarget:" + mtarget.getTaskid());
                    addListener(mtarget, listener);
                    // Check the provided ServerCallStreamObserver to see if it is still ready to accept more messages.
                    if (serverCallStreamObserver.isReady()) {
                        // Signal the sender to send another request. As long as isReady() stays true, the server will keep
                        // cycling through the loop of onNext() -> request(1)...onNext() -> request(1)... until the client runs
                        // out of messages and ends the loop (via onCompleted()).
                        //
                        // If request() was called here with the argument of more than 1, the server might runs out of receive
                        // buffer space, and isReady() will turn false. When the receive buffer has sufficiently drained,
                        // isReady() will turn true, and the serverCallStreamObserver's onReadyHandler will be called to restart
                        // the message pump.
                        // serverCallStreamObserver.request(1);
                    } else {
                        // If not, note that back-pressure has begun.
                        onReadyHandler.wasReady = false;
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    logger.error(throwable.getMessage(), throwable);
                    responseObserver.onError(Status.UNKNOWN.withDescription("Error handling request").withCause(throwable).asException());
                }
            }

            @Override
            public void onError(Throwable t) {
                // End the response stream if the client presents an error.
                t.printStackTrace();
                if (!serverCallStreamObserver.isCancelled()) {
                    responseObserver.onCompleted();
                }
            }

            @Override
            public void onCompleted() {
                // Signal the end of work when the client ends the request stream.
                logger.info("COMPLETED");
                responseObserver.onCompleted();
                registerMonitorEventHook.closeSession();
            }
        };
    }

    public static String addListener(MonotorTarget mtarget, RealtimeLoggerCollectorAppender.LoggerCollectorAppenderListener listener) {
        StringBuffer targetAppenderName = new StringBuffer();
        RealtimeLoggerCollectorAppender.LoggingEventMeta evtMeta = new RealtimeLoggerCollectorAppender.LoggingEventMeta();
        evtMeta.logTypeVal = mtarget.getLogType().typeKind;
        targetAppenderName.append(mtarget.getLogType().getValue()).append("-");
        if (mtarget.getTaskid() != null) {
            if (!(mtarget.getLogType() == LogType.FULL)) {
                throw new IllegalArgumentException("target logtype:" + mtarget.getLogType() + " is illegal");
            }
            evtMeta.taskid = mtarget.getTaskid();
            targetAppenderName.append(mtarget.getTaskid());
        } else {
            if (!(mtarget.getLogType() == LogType.INCR_SEND || mtarget.getLogType() == LogType.INCR)) {
                throw new IllegalArgumentException("target logtype:" + mtarget.getLogType() + " is illegal");
            }
            evtMeta.collection = mtarget.getCollection();
            targetAppenderName.append(mtarget.getCollection());
        }
        String target = targetAppenderName.toString();
        RealtimeLoggerCollectorAppender.addListener(target, evtMeta, listener);
        return target;
    }

    /**
     * 分布式任务启动时候，同步rpc的方式向 assemble节点注册注册
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void initTask(com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection request,
                         io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.common.Empty> responseObserver) {

        List<Supplier<FullbuildPhase>> phaseJudgemment = Lists.newArrayList();
        phaseJudgemment.add(() -> request.getDumpPhase().getTablesDumpCount() > 0 ? FullbuildPhase.FullDump : null);
        phaseJudgemment.add(() -> request.getJoinPhase().getTaskStatusCount() > 0 ? FullbuildPhase.JOIN : null);
        phaseJudgemment.add(() -> request.getBuildPhase().getNodeBuildStatusCount() > 0 ? FullbuildPhase.BUILD : null);
        phaseJudgemment.add(() -> request.getIndexBackFlowPhaseStatus().getNodesStatusCount() > 0 ? FullbuildPhase.IndexBackFlow : null);

        ExecutePhaseRange phaseRange = createPhaseRange(phaseJudgemment);
        PhaseStatusCollection statusCollection = LogCollectorClient.convert(request, phaseRange);
        statusCollection.flushStatus2Local();
        TrackableExecuteInterceptor.initialTaskPhase(statusCollection);

        responseObserver.onNext(com.qlangtech.tis.rpc.grpc.log.common.Empty.newBuilder().build());
        responseObserver.onCompleted();

    }

    private static ExecutePhaseRange createPhaseRange(List<Supplier<FullbuildPhase>> phaseJudgemment) {
        FullbuildPhase start = null;
        FullbuildPhase end = null;
        for (Supplier<FullbuildPhase> s : phaseJudgemment) {
            if ((start = s.get()) != null) {
                break;
            }
        }
        List<Supplier<FullbuildPhase>> reversed = Lists.newArrayList(phaseJudgemment);
        Collections.reverse(reversed);
        for (Supplier<FullbuildPhase> s : reversed) {
            if ((end = s.get()) != null) {
                break;
            }
        }
        return new ExecutePhaseRange(start, end);
    }


    @Override
    public void buildPhraseStatus(PBuildPhaseStatusParam request, StreamObserver<PPhaseStatusCollection> responseObserver) {
        final int taskid = (int) request.getTaskid();
        logger.info("receive taskid:" + taskid + " ge relevant phaseStatusSet apply");
        final ServerCallStreamObserver<PPhaseStatusCollection> serverCallStreamObserver = (ServerCallStreamObserver<PPhaseStatusCollection>) responseObserver;
        // 先查一下数据库判断该任务是否已经结束
        final AtomicBoolean wasReady = new AtomicBoolean();
        serverCallStreamObserver.setOnReadyHandler(() -> {
            if (serverCallStreamObserver.isReady() && !wasReady.get()) {
                wasReady.set(true);
                // Signal the request sender to send one message. This happens when isReady() turns true, signaling that
                // the receive buffer has enough free space to receive more messages. Calling request() serves to prime
                // the message pump.
                serverCallStreamObserver.request(1);
                PhaseStatusCollection phaseStatusSet = null;
                do {
                    phaseStatusSet = TrackableExecuteInterceptor.getTaskPhaseReference(taskid);
                    if (phaseStatusSet == null) {
                        phaseStatusSet = IndexSwapTaskflowLauncher.loadPhaseStatusFromLocal(taskid);
                    }
                } while (serverCallStreamObserver.isReady() && isStatusNotPresent(phaseStatusSet));
                logger.info("ready to send taskid:" + taskid + "relevant stat info");
                while (serverCallStreamObserver.isReady()) {
                    serverCallStreamObserver.onNext(LogCollectorClient.convertPP(phaseStatusSet));
                    if (phaseStatusSet.isComplete()) {
                        // 如果已经完成了就立即停止发送消息
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
                logger.info("stopto monitor taskid:" + taskid + "relevant stat info");
                serverCallStreamObserver.onCompleted();
            }
        });
    }

    private boolean isStatusNotPresent(PhaseStatusCollection phaseStatusSet) {
        boolean isStatusNotPresent = phaseStatusSet == null;
        if (isStatusNotPresent) {
            try {
                Thread.sleep(1000l);
            } catch (InterruptedException e) {
            }
        }
        return isStatusNotPresent;
    }
}
