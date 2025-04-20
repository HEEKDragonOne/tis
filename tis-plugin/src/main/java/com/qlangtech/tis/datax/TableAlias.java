package com.qlangtech.tis.datax;

import com.alibaba.fastjson.annotation.JSONField;
import com.qlangtech.tis.extension.Describable;
import com.qlangtech.tis.extension.Descriptor;
import com.qlangtech.tis.extension.impl.XmlFile;
import com.qlangtech.tis.plugin.KeyedPluginStore;
import com.qlangtech.tis.util.IPluginContext;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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

/**
 * 表别名
 */
public class TableAlias implements Describable<TableAlias> {
    public static final String KEY_FROM_TABLE_NAME = "fromTableName";
    private String from;
    private String to;

    public static List<TableAlias> testTabAlias;

    private static KeyedPluginStore.AppKey createAppSourceKey(IPluginContext context, String appName) {
        return new KeyedPluginStore.AppKey(context, StoreResourceType.DataApp, appName, TableAlias.class);
    }

    /**
     * 保存
     *
     * @param context
     * @param appName
     * @param tableMaps
     */
    public static void save(IPluginContext context, String appName, List<TableAlias> tableMaps) {
        try {
            createAppSourceKey(context, appName).getSotreFile().write(tableMaps, Collections.emptySet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载
     *
     * @param context
     * @param appName
     * @return
     */
    public static List<TableAlias> load(IPluginContext context, String appName) {
        if (testTabAlias != null) {
            return testTabAlias;
        }
        try {
            XmlFile sotreFile = createAppSourceKey(context, appName).getSotreFile();
            if (!sotreFile.exists()) {
                return Collections.emptyList();
            }
            return (List<TableAlias>) sotreFile.unmarshal(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TableAlias() {
    }

    public boolean hasNotUseAlias() {
        return StringUtils.equalsIgnoreCase(this.getFrom(), this.getTo());
    }

    public TableAlias(String from) {
        this.from = from;
        // 如果使用oracle的表，表名中可能出现点，所以要将它去掉
        int indexOfCommon = StringUtils.indexOf(from, ".");
        this.to = indexOfCommon > -1 ? StringUtils.substring(from, indexOfCommon + 1) : from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    /**
     * 不需要实例化
     *
     * @return
     */
    @JSONField(serialize = false)
    @Override
    public Descriptor<TableAlias> getDescriptor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "TableMap{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
