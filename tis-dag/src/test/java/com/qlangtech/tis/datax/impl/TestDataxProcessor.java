/**
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.qlangtech.tis.datax.impl;

import com.qlangtech.tis.datax.IDataxProcessor;
import com.qlangtech.tis.sql.parser.stream.generate.BasicTestCase;
import com.qlangtech.tis.util.IPluginContext;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-05-15 13:59
 **/
public class TestDataxProcessor extends BasicTestCase {

    /**
     * 加载一个空的默认DataxProcessor
     */
    public void testLoad() {
        IPluginContext pluginContext = EasyMock.createMock("pluginContext", IPluginContext.class);
        String randomDataXName = RandomStringUtils.randomAlphabetic(4);
        EasyMock.replay(pluginContext);
        IDataxProcessor dataxProcessor = DataxProcessor.load(pluginContext, randomDataXName);
        assertNotNull(dataxProcessor);
        EasyMock.verify(pluginContext);
    }
}
