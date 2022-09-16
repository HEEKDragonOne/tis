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

package com.qlangtech.tis.plugin.credentials;

import com.alibaba.citrus.turbine.Context;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qlangtech.tis.TIS;
import com.qlangtech.tis.config.ParamsConfig;
import com.qlangtech.tis.extension.Descriptor;
import com.qlangtech.tis.extension.ExtensionList;
import com.qlangtech.tis.extension.impl.XmlFile;
import com.qlangtech.tis.plugin.IPluginStore;
import com.qlangtech.tis.plugin.IRepositoryResource;
import com.qlangtech.tis.plugin.SetPluginsResult;
import com.qlangtech.tis.util.IPluginContext;
import com.qlangtech.tis.util.UploadPluginMeta;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2021-12-07 17:48
 **/
public class ParamsConfigPluginStore implements IPluginStore<ParamsConfig> {

    private static final File paramsCfgDir;

    static {
        try {
            paramsCfgDir = new File(TIS.pluginCfgRoot, ParamsConfig.CONTEXT_PARAMS_CFG);
            FileUtils.forceMkdir(paramsCfgDir);
        } catch (IOException e) {
            throw new RuntimeException("can not create dir:" + ParamsConfig.CONTEXT_PARAMS_CFG, e);
        }
    }

//    public static void main(String[] args) {
//        File root = new File(".");
//        Path rootPath = root.toPath();
//        Iterator<File> fit = FileUtils.iterateFiles(root, null, true);
//        while (fit.hasNext()) {
//            File f = fit.next();
//            System.out.println(f.getAbsolutePath());
//            System.out.println(rootPath.relativize(f.toPath()));
//        }
//    }

    private final UploadPluginMeta pluginMeta;

    public ParamsConfigPluginStore(UploadPluginMeta pluginMeta) {
        if (pluginMeta == null) {
            throw new IllegalArgumentException("param pluginMeta can not be null");
        }
        this.pluginMeta = pluginMeta;
    }

    @Override
    public List<IRepositoryResource> getAll() {
        ExtensionList<Descriptor<ParamsConfig>> descs
                = TIS.get().getDescriptorList(ParamsConfig.class);
        return descs.stream()
                .map((desc) -> ParamsConfig.getTargetPluginStore(desc.getDisplayName(), false))
                .filter((rr) -> rr != null && rr.getTargetFile().exists()).collect(Collectors.toList());
    }

    @Override
    public List<ParamsConfig> getPlugins() {
        // List<ParamsConfig> plugins = Lists.newArrayList();
        return visitAllPluginStore((pluginStore) -> {
            return pluginStore.getPlugins();
        });
        // return plugins;
    }


    private <TT> TT visitAllPluginStore(Function<IPluginStore<ParamsConfig>, TT> func) {
        String[] childFiles = paramsCfgDir.list();
        UploadPluginMeta.TargetDesc targetDesc = this.pluginMeta.getTargetDesc();
        TT result = null;
        for (String childFile : childFiles) {
            if (!StringUtils.equals(targetDesc.matchTargetPluginDescName, childFile)) {
                continue;
            }
            IPluginStore<ParamsConfig> pluginStore = ParamsConfig.getChildPluginStore(childFile);
            result = func.apply(pluginStore);
            return result;
//            if (result != null) {
//                return result;
//            } else {
//                return null;
//            }
        }
        throw new IllegalStateException("can not find any match param config store:" + targetDesc);
    }


    @Override
    public ParamsConfig getPlugin() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cleanPlugins() {
        visitAllPluginStore((ps) -> {
            ps.cleanPlugins();
            return null;
        });
    }

    @Override
    public List<Descriptor<ParamsConfig>> allDescriptor() {
        List<Descriptor<ParamsConfig>> descs = Lists.newArrayList();
        visitAllPluginStore((ps) -> {
            descs.addAll(ps.allDescriptor());
            return null;
        });
        return descs;
    }

    @Override
    public ParamsConfig find(String name, boolean throwNotFoundErr) {
        ParamsConfig cfg = visitAllPluginStore((ps) -> {
            return ps.find(name, throwNotFoundErr);
        });
        return cfg;
    }

    @Override
    public SetPluginsResult setPlugins(IPluginContext pluginContext, Optional<Context> context
            , List<Descriptor.ParseDescribable<ParamsConfig>> dlist, boolean update) {

        Map<String, List<Descriptor.ParseDescribable<ParamsConfig>>> desc2Plugin = Maps.newHashMap();
        String descName = null;
        ParamsConfig paramCfg = null;
        List<Descriptor.ParseDescribable<ParamsConfig>> plugins = null;
        boolean cfgChanged = false;
        for (Descriptor.ParseDescribable<ParamsConfig> p : dlist) {
            paramCfg = p.getInstance();
            descName = paramCfg.getDescriptor().getDisplayName();
            plugins = desc2Plugin.get(descName);
            if (plugins == null) {
                plugins = Lists.newArrayList();
                desc2Plugin.put(descName, plugins);
            }
            plugins.add(p);
        }

        for (Map.Entry<String, List<Descriptor.ParseDescribable<ParamsConfig>>> entry : desc2Plugin.entrySet()) {
            IPluginStore<ParamsConfig> childPluginStore = ParamsConfig.getChildPluginStore(entry.getKey());
            if (childPluginStore.setPlugins(pluginContext, context, entry.getValue(), true).cfgChanged) {
                cfgChanged = true;
            }
        }

        return new SetPluginsResult(true, cfgChanged);
    }

    @Override
    public void copyConfigFromRemote() {
        IPluginStore<ParamsConfig> childPluginStore = ParamsConfig.getTargetPluginStore(pluginMeta.getTargetDesc().impl);
        childPluginStore.copyConfigFromRemote();
    }

    @Override
    public XmlFile getTargetFile() {
        IPluginStore<ParamsConfig> childPluginStore = ParamsConfig.getTargetPluginStore(pluginMeta.getTargetDesc().impl);
        return childPluginStore.getTargetFile();
    }

    @Override
    public long getWriteLastModifyTimeStamp() {
        IPluginStore<ParamsConfig> childPluginStore = ParamsConfig.getTargetPluginStore(pluginMeta.getTargetDesc().impl);
        return childPluginStore.getWriteLastModifyTimeStamp();
    }

}
