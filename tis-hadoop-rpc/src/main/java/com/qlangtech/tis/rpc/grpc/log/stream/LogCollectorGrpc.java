package com.qlangtech.tis.rpc.grpc.log.stream;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.33.0)",
    comments = "Source: log-collector.proto")
public final class LogCollectorGrpc {

  private LogCollectorGrpc() {}

  public static final String SERVICE_NAME = "stream.LogCollector";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.qlangtech.tis.rpc.grpc.log.stream.PMonotorTarget,
      com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState> getRegisterMonitorEventMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterMonitorEvent",
      requestType = com.qlangtech.tis.rpc.grpc.log.stream.PMonotorTarget.class,
      responseType = com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<com.qlangtech.tis.rpc.grpc.log.stream.PMonotorTarget,
      com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState> getRegisterMonitorEventMethod() {
    io.grpc.MethodDescriptor<com.qlangtech.tis.rpc.grpc.log.stream.PMonotorTarget, com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState> getRegisterMonitorEventMethod;
    if ((getRegisterMonitorEventMethod = LogCollectorGrpc.getRegisterMonitorEventMethod) == null) {
      synchronized (LogCollectorGrpc.class) {
        if ((getRegisterMonitorEventMethod = LogCollectorGrpc.getRegisterMonitorEventMethod) == null) {
          LogCollectorGrpc.getRegisterMonitorEventMethod = getRegisterMonitorEventMethod =
              io.grpc.MethodDescriptor.<com.qlangtech.tis.rpc.grpc.log.stream.PMonotorTarget, com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterMonitorEvent"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qlangtech.tis.rpc.grpc.log.stream.PMonotorTarget.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState.getDefaultInstance()))
              .setSchemaDescriptor(new LogCollectorMethodDescriptorSupplier("RegisterMonitorEvent"))
              .build();
        }
      }
    }
    return getRegisterMonitorEventMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam,
      com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection> getBuildPhraseStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "BuildPhraseStatus",
      requestType = com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam.class,
      responseType = com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam,
      com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection> getBuildPhraseStatusMethod() {
    io.grpc.MethodDescriptor<com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam, com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection> getBuildPhraseStatusMethod;
    if ((getBuildPhraseStatusMethod = LogCollectorGrpc.getBuildPhraseStatusMethod) == null) {
      synchronized (LogCollectorGrpc.class) {
        if ((getBuildPhraseStatusMethod = LogCollectorGrpc.getBuildPhraseStatusMethod) == null) {
          LogCollectorGrpc.getBuildPhraseStatusMethod = getBuildPhraseStatusMethod =
              io.grpc.MethodDescriptor.<com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam, com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "BuildPhraseStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection.getDefaultInstance()))
              .setSchemaDescriptor(new LogCollectorMethodDescriptorSupplier("BuildPhraseStatus"))
              .build();
        }
      }
    }
    return getBuildPhraseStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection,
      com.qlangtech.tis.rpc.grpc.log.common.Empty> getInitTaskMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InitTask",
      requestType = com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection.class,
      responseType = com.qlangtech.tis.rpc.grpc.log.common.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection,
      com.qlangtech.tis.rpc.grpc.log.common.Empty> getInitTaskMethod() {
    io.grpc.MethodDescriptor<com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection, com.qlangtech.tis.rpc.grpc.log.common.Empty> getInitTaskMethod;
    if ((getInitTaskMethod = LogCollectorGrpc.getInitTaskMethod) == null) {
      synchronized (LogCollectorGrpc.class) {
        if ((getInitTaskMethod = LogCollectorGrpc.getInitTaskMethod) == null) {
          LogCollectorGrpc.getInitTaskMethod = getInitTaskMethod =
              io.grpc.MethodDescriptor.<com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection, com.qlangtech.tis.rpc.grpc.log.common.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "InitTask"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.qlangtech.tis.rpc.grpc.log.common.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new LogCollectorMethodDescriptorSupplier("InitTask"))
              .build();
        }
      }
    }
    return getInitTaskMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static LogCollectorStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LogCollectorStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LogCollectorStub>() {
        @java.lang.Override
        public LogCollectorStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LogCollectorStub(channel, callOptions);
        }
      };
    return LogCollectorStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static LogCollectorBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LogCollectorBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LogCollectorBlockingStub>() {
        @java.lang.Override
        public LogCollectorBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LogCollectorBlockingStub(channel, callOptions);
        }
      };
    return LogCollectorBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static LogCollectorFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LogCollectorFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LogCollectorFutureStub>() {
        @java.lang.Override
        public LogCollectorFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LogCollectorFutureStub(channel, callOptions);
        }
      };
    return LogCollectorFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class LogCollectorImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * 订阅日志信息
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.stream.PMonotorTarget> registerMonitorEvent(
        io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState> responseObserver) {
      return asyncUnimplementedStreamingCall(getRegisterMonitorEventMethod(), responseObserver);
    }

    /**
     * <pre>
     * 订阅全量/DataFlow构建过程中各阶段执行的状态信息
     * </pre>
     */
    public void buildPhraseStatus(com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam request,
        io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection> responseObserver) {
      asyncUnimplementedUnaryCall(getBuildPhraseStatusMethod(), responseObserver);
    }

    /**
     * <pre>
     * 分布式环境下，启动一个同步任务先需要向assemble节点注册所有子任务
     * </pre>
     */
    public void initTask(com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection request,
        io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.common.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getInitTaskMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRegisterMonitorEventMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                com.qlangtech.tis.rpc.grpc.log.stream.PMonotorTarget,
                com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState>(
                  this, METHODID_REGISTER_MONITOR_EVENT)))
          .addMethod(
            getBuildPhraseStatusMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam,
                com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection>(
                  this, METHODID_BUILD_PHRASE_STATUS)))
          .addMethod(
            getInitTaskMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection,
                com.qlangtech.tis.rpc.grpc.log.common.Empty>(
                  this, METHODID_INIT_TASK)))
          .build();
    }
  }

  /**
   */
  public static final class LogCollectorStub extends io.grpc.stub.AbstractAsyncStub<LogCollectorStub> {
    private LogCollectorStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LogCollectorStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LogCollectorStub(channel, callOptions);
    }

    /**
     * <pre>
     * 订阅日志信息
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.stream.PMonotorTarget> registerMonitorEvent(
        io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getRegisterMonitorEventMethod(), getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     * 订阅全量/DataFlow构建过程中各阶段执行的状态信息
     * </pre>
     */
    public void buildPhraseStatus(com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam request,
        io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getBuildPhraseStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 分布式环境下，启动一个同步任务先需要向assemble节点注册所有子任务
     * </pre>
     */
    public void initTask(com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection request,
        io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.common.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInitTaskMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class LogCollectorBlockingStub extends io.grpc.stub.AbstractBlockingStub<LogCollectorBlockingStub> {
    private LogCollectorBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LogCollectorBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LogCollectorBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 订阅全量/DataFlow构建过程中各阶段执行的状态信息
     * </pre>
     */
    public java.util.Iterator<com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection> buildPhraseStatus(
        com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam request) {
      return blockingServerStreamingCall(
          getChannel(), getBuildPhraseStatusMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 分布式环境下，启动一个同步任务先需要向assemble节点注册所有子任务
     * </pre>
     */
    public com.qlangtech.tis.rpc.grpc.log.common.Empty initTask(com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection request) {
      return blockingUnaryCall(
          getChannel(), getInitTaskMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class LogCollectorFutureStub extends io.grpc.stub.AbstractFutureStub<LogCollectorFutureStub> {
    private LogCollectorFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LogCollectorFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LogCollectorFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 分布式环境下，启动一个同步任务先需要向assemble节点注册所有子任务
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.qlangtech.tis.rpc.grpc.log.common.Empty> initTask(
        com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection request) {
      return futureUnaryCall(
          getChannel().newCall(getInitTaskMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_BUILD_PHRASE_STATUS = 0;
  private static final int METHODID_INIT_TASK = 1;
  private static final int METHODID_REGISTER_MONITOR_EVENT = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final LogCollectorImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(LogCollectorImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_BUILD_PHRASE_STATUS:
          serviceImpl.buildPhraseStatus((com.qlangtech.tis.rpc.grpc.log.stream.PBuildPhaseStatusParam) request,
              (io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection>) responseObserver);
          break;
        case METHODID_INIT_TASK:
          serviceImpl.initTask((com.qlangtech.tis.rpc.grpc.log.stream.PPhaseStatusCollection) request,
              (io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.common.Empty>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER_MONITOR_EVENT:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.registerMonitorEvent(
              (io.grpc.stub.StreamObserver<com.qlangtech.tis.rpc.grpc.log.stream.PExecuteState>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class LogCollectorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    LogCollectorBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.qlangtech.tis.rpc.grpc.log.stream.LogCollectorProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("LogCollector");
    }
  }

  private static final class LogCollectorFileDescriptorSupplier
      extends LogCollectorBaseDescriptorSupplier {
    LogCollectorFileDescriptorSupplier() {}
  }

  private static final class LogCollectorMethodDescriptorSupplier
      extends LogCollectorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    LogCollectorMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (LogCollectorGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new LogCollectorFileDescriptorSupplier())
              .addMethod(getRegisterMonitorEventMethod())
              .addMethod(getBuildPhraseStatusMethod())
              .addMethod(getInitTaskMethod())
              .build();
        }
      }
    }
    return result;
  }
}
