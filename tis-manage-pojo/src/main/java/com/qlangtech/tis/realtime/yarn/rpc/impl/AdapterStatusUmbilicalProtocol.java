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

package com.qlangtech.tis.realtime.yarn.rpc.impl;

import com.qlangtech.tis.fullbuild.phasestatus.PhaseStatusCollection;
import com.qlangtech.tis.fullbuild.phasestatus.impl.BuildSharedPhaseStatus;
import com.qlangtech.tis.fullbuild.phasestatus.impl.DumpPhaseStatus;
import com.qlangtech.tis.fullbuild.phasestatus.impl.JoinPhaseStatus;
import com.qlangtech.tis.realtime.yarn.rpc.*;
import org.apache.commons.lang.NotImplementedException;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-05-03 14:13
 **/
public class AdapterStatusUmbilicalProtocol implements IncrStatusUmbilicalProtocol {
    @Override
    public PingResult ping() {
        throw new NotImplementedException();
    }

    @Override
    public void initSynJob(PhaseStatusCollection buildStatus) {
        throw new NotImplementedException();
    }

    @Override
    public MasterJob reportStatus(UpdateCounterMap upateCounter) {
        throw new NotImplementedException();
    }

    @Override
    public void nodeLaunchReport(LaunchReportInfo launchReportInfo) {
        throw new NotImplementedException();
    }

    @Override
    public void reportJoinStatus(JoinPhaseStatus.JoinTaskStatus joinTaskStatus) {
        throw new NotImplementedException();
    }

    @Override
    public void reportDumpTableStatus(DumpPhaseStatus.TableDumpStatus tableDumpStatus) {
        throw new NotImplementedException();
    }

    @Override
    public void reportBuildIndexStatus(BuildSharedPhaseStatus buildStatus) {
        throw new NotImplementedException();
    }
}
