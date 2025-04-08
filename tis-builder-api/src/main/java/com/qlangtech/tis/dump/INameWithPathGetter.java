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
package com.qlangtech.tis.dump;

import com.qlangtech.tis.sql.parser.tuple.creator.EntityName;

import java.util.Objects;
import java.util.Optional;

import static com.qlangtech.tis.fullbuild.indexbuild.IDumpTable.DEFAULT_DATABASE_NAME;

/**
 * 获取和路径相关的值，HiveRemoveHistoryDataTask的刪除和有table相關的路徑，還有index相關的build出來的倒排索引內容，所以兩種路徑的獲取方式是不相同的
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2017年6月26日
 */
@FunctionalInterface
public interface INameWithPathGetter {

    public static INameWithPathGetter create(EntityName entity) {
//        final String dbName = entity.getDbname();
//        return () ->
//                (dbName != null ? dbName : DEFAULT_DATABASE_NAME) + "/" + entity.getTabName();
        return create(Optional.ofNullable(entity.getDbname()), entity.getTabName());
    }

    public static INameWithPathGetter create(Optional<String> dbName, String table) {
        Objects.requireNonNull(dbName, "dbName can not be null");
        Objects.requireNonNull(table, "param table can not be null");
        return () ->
                dbName.orElse(DEFAULT_DATABASE_NAME) + "/" + table;
    }

    public String getNameWithPath();
}
