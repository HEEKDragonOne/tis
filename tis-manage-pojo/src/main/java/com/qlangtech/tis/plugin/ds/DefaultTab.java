package com.qlangtech.tis.plugin.ds;

import com.qlangtech.tis.runtime.module.misc.IMessageHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2022-10-17 09:47
 **/
public final class DefaultTab implements ISelectedTab {
    private final String dataXName;
    private final List<CMeta> writerCols;

    public DefaultTab(String dataXName, List<CMeta> writerCols) {
        this.dataXName = dataXName;
        this.writerCols = writerCols;
    }

    @Override
    public List<String> getPrimaryKeys() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IColMetaGetter> overwriteCols(IMessageHandler pluginCtx, Optional<IReaderSource> readerSource) {
        throw new UnsupportedOperationException();
    }

    public DefaultTab(String tabName) {
        this(tabName, Collections.emptyList());
    }

    @Override
    public String getName() {
        return dataXName;
    }

    @Override
    public List<CMeta> getCols() {
        return writerCols;
    }
}
