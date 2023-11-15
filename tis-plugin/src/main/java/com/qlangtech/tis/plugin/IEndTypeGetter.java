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

package com.qlangtech.tis.plugin;

import com.qlangtech.tis.extension.impl.IOUtils;

import java.util.Set;

/**
 * 端类型
 *
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2023-03-05 10:13
 **/
public interface IEndTypeGetter {

    String KEY_END_TYPE = "endType";
    String KEY_SUPPORT_ICON = "supportIcon";

    public static void main(String[] args) {
        for (EndType value : EndType.values()) {
            System.out.print(value.val + ",");
        }
    }

    /**
     * 取得数据端类型
     *
     * @return
     */
    IDataXEndTypeGetter.EndType getEndType();

    /**
     * 端类型
     */
    enum EndType {
        Greenplum("greenplum"), MySQL("mysql", true), Postgres("pg",true), Oracle("oracle",true) //
        , ElasticSearch("es",true), MongoDB("mongoDB",true) //
        , StarRocks("starRocks",true), Doris("doris",true) //
        , Clickhouse("clickhouse",true), Hudi("hudi") //, AliyunOSS("aliyunOSS")
        , TDFS("t-dfs",true) //
        , Cassandra("cassandra") //, HDFS("hdfs")
        , SqlServer("sqlServer",true), TiDB("TiDB",true) //
        , RocketMQ("rocketMq",true), Kafka("kafka",true), DataFlow("dataflow") //
        , DaMeng("daMeng"), AliyunODPS("aliyunOdps"), HiveMetaStore("hms", true), RabbitMQ("rabbitmq");
        private final String val;
        private final boolean containICON;

        public static EndType parse(String endType) {
            for (EndType end : EndType.values()) {
                if (end.val.equals(endType)) {
                    return end;
                }
            }
            throw new IllegalStateException("illegal endType:" + endType);
        }

        EndType(String val) {
            this(val, false);
        }

        EndType(String val, boolean containICON) {
            this.val = val;
            this.containICON = containICON;
        }

        public String getVal() {
            return this.val;
        }

        private Icon icon;

        public Icon getIcon() {
            if (!this.containICON) {
                return icon;
            }

            if (icon == null) {
                icon = new Icon() {
                    private String loadIconWithSuffix(String theme) {
                        return IOUtils.loadResourceFromClasspath(IEndTypeGetter.class
                                , "endtype/icon/" + val + "/" + theme + ".svg", true);
                    }
                    @Override
                    public String fillType() {
                        return loadIconWithSuffix("fill");
                    }

                    @Override
                    public String outlineType() {
                        return loadIconWithSuffix("outline");
                    }
                };
            }
            return icon;
        }

        public boolean containIn(Set<String> endTypes) {
            return endTypes.contains(this.getVal());
        }
    }


    public interface Icon {
        public String fillType();

        public String outlineType();
    }
}
