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

import com.qlangtech.tis.plugin.ds.ISelectedTab;

import java.util.Map;

/**
 * @author 百岁（baisui@qlangtech.com）
 * @date 2021-04-15 16:55
 */
public interface IDataxReaderContext extends IDataxContext {

    /**
     * 对应的DataSourceId
     *
     * @return
     */
    String getReaderContextId();

    public String getTaskName();

    /**
     * 如果是mysql导入，则为源数据库表的表名
     *
     * @return
     */
    String getSourceEntityName();

    /**
     * 原表名称'getSourceEntityName' 方法返回的多escape符号包裹
     *
     * @return
     */
    String getSourceTableName();


    default IDataxProcessor.TableMap createTableMap(TableAliasMapper tabAlias, Map<String, ISelectedTab> selectedTabs
    ) {

        TableAlias tableAlias = tabAlias.get(this.getSourceTableName());
        if (tableAlias == null) {
//            throw new IllegalStateException("sourceTable:" + this.getSourceTableName() + " can not find " +
//                    "relevant 'tableAlias' keys:[" + tabAlias.getFromTabDesc() + "]");
            tableAlias = new TableAlias(this.getSourceTableName());
        }
        ISelectedTab selectedTab = selectedTabs.get(this.getSourceTableName());
        if (selectedTab == null) {
            throw new IllegalStateException("sourceTable:" + this.getSourceTableName() + " can not find " +
                    "relevant '" + ISelectedTab.class.getSimpleName() + "' keys:[" + String.join(",",
                    selectedTabs.keySet()) + "]");
        }
        IDataxProcessor.TableMap tableMap = createTableMap(tableAlias, selectedTab);
        return tableMap;
    }

    default IDataxProcessor.TableMap createTableMap(TableAlias tableAlias, ISelectedTab selectedTab) {
        return new IDataxProcessor.TableMap(tableAlias, selectedTab);
    }
}
