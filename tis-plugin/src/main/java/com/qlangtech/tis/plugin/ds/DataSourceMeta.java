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
package com.qlangtech.tis.plugin.ds;


import com.qlangtech.tis.extension.Describable;
import com.qlangtech.tis.sql.parser.tuple.creator.EntityName;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 数据源meta信息获取
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2021-04-07 15:51
 */
public interface DataSourceMeta extends Describable.IRefreshable {

    default String getEscapeChar() {
        return StringUtils.EMPTY;
    }

    /**
     * Get all the tables in dataBase
     *
     * @return
     */
    default TableInDB getTablesInDB() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get table column metaData list
     *
     * @param table
     * @return
     */
    default List<ColumnMetaData> getTableMetadata(EntityName table) throws TableNotFoundException {
        throw new UnsupportedOperationException();
    }

    /**
     * Get table column metaData list
     *
     * @param table
     * @return
     */
    default List<ColumnMetaData> getTableMetadata(JDBCConnection conn, EntityName table) throws TableNotFoundException {
        throw new UnsupportedOperationException();
    }

    public class JDBCConnection implements AutoCloseable {
        private final Connection conn;
        private final String url;

        public JDBCConnection(Connection conn, String url) {
            this.conn = conn;
            this.url = url;
        }

        public Statement createStatement() throws SQLException {
            return this.conn.createStatement();
        }

        public Connection getConnection() {
            return this.conn;
        }

        public String getUrl() {
            return this.url;
        }

        @Override
        public void close() throws SQLException {
            this.conn.close();
        }
    }
}
