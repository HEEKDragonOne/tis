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

package com.qlangtech.tis.datax.preview;

import java.util.List;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2024-07-25 09:22
 **/
public class PreviewRowsData {
    private final String[] header;
    private final List<String[]> rows;

    public PreviewRowsData(String[] header, List<String[]> rows) {
        this.header = header;
        this.rows = rows;
    }

    public String[] getHeader() {
        return this.header;
    }

    public List<String[]> getRows() {
        return this.rows;
    }
}
