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
package com.qlangtech.tis.fs;

/**
 * 底层文件系统抽象，目前支持hdfs，将来需要支持AliOss，亚马逊文件系统
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2018年11月23日
 */
public interface ITISFileSystemFactory {
    String KEY_FIELD_NAME_FS_NAME = "fsName";
    // public String getRootDir();
    public String getFSAddress();
    public ITISFileSystem getFileSystem();
}
