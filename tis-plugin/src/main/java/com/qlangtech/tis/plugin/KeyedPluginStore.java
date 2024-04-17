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
package com.qlangtech.tis.plugin;

import com.alibaba.citrus.turbine.Context;
import com.google.common.collect.Lists;
import com.qlangtech.tis.TIS;
import com.qlangtech.tis.datax.impl.DataxReader;
import com.qlangtech.tis.extension.Describable;
import com.qlangtech.tis.extension.Descriptor;
import com.qlangtech.tis.extension.impl.XmlFile;
import com.qlangtech.tis.manage.common.CenterResource;
import com.qlangtech.tis.manage.common.TisUTF8;
import com.qlangtech.tis.solr.common.DOMUtil;
import com.qlangtech.tis.util.IPluginContext;
import com.qlangtech.tis.util.PluginMeta;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 百岁（baisui@qlangtech.com）
 * @date 2020/04/13
 */
public class KeyedPluginStore<T extends Describable> extends PluginStore<T> {
    public static final String TMP_DIR_NAME = ".tmp/";
    public static final String KEY_EXEC_ID = "execId";
    // private static final Pattern DATAX_UPDATE_PATH = Pattern.compile("/x/(" + ValidatorCommons.pattern_identity +
    // ")/update");

    private static final Pattern DATAX_UPDATE_PATH = Pattern.compile("\\?" + KEY_EXEC_ID + "=.+?");

    public transient final Key key;

    public static void main(String[] args) {
        Matcher matcher = DATAX_UPDATE_PATH.matcher("/offline/wf_profile_update/update?execId=e5bad69e-d519-5799-c798"
                + "-f593eb0555f2#writer");
        System.out.println(matcher.find());
    }

    public static <TT extends Describable> KeyedPluginStore<TT> getPluginStore(DataxReader.SubFieldFormAppKey<TT> subFieldFormKey) {
        return (KeyedPluginStore<TT>) TIS.dataXReaderSubFormPluginStore.get(subFieldFormKey);
    }

    static File getLastModifyToken(Key appKey) {
        File appDir = getSubPathDir(appKey);
        File lastModify = new File(appDir, CenterResource.KEY_LAST_MODIFIED_EXTENDION);
        return lastModify;
    }

    public static PluginMetas getAppAwarePluginMetas(boolean isDB, String name) {
        return getAppAwarePluginMetas(StoreResourceType.parse(isDB), name);
    }

    public static PluginMetas getAppAwarePluginMetas(StoreResourceType resourceType, String name) {
        return getAppAwarePluginMetas(resourceType, name, true);
    }

    /**
     * 取得某个应用下面相关的插件元数据信息用于分布式任务同步用
     *
     * @return
     */
    public static PluginMetas getAppAwarePluginMetas(StoreResourceType resourceType, String name, boolean resolveMeta) {
        AppKey appKey = new AppKey(null, resourceType, name, (PluginClassCategory) null);
        File appDir = getSubPathDir(appKey);
        File lastModify = getLastModifyToken(appKey);// new File(appDir, CenterResource.KEY_LAST_MODIFIED_EXTENDION);
        long lastModfiyTimeStamp = -1;
        Set<PluginMeta> metas = Collections.emptySet();

        try {
            if (appDir.exists()) {
                if (lastModify.exists()) {
                    lastModfiyTimeStamp = Long.parseLong(FileUtils.readFileToString(lastModify, TisUTF8.get()));
                }
                if (resolveMeta) {
                    Iterator<File> files = FileUtils.iterateFiles(appDir, new String[]{DOMUtil.XML_RESERVED_PREFIX}, true);
                    metas = ComponentMeta.loadPluginMeta(() -> {
                        return Lists.newArrayList(files);
                    });
                }
            }
            return new PluginMetas(appDir, metas, lastModfiyTimeStamp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static class PluginMetas {
        // 全局配置文件对应的最近更新Key
        public static final String KEY_GLOBAL_PLUGIN_STORE = "globalPluginStore";
        //　plugin tpi 包的最近更新时间对应的Key
        public static final String KEY_PLUGIN_META = "pluginMetas";
        // app应用对应的最近更新时间
        public static final String KEY_APP_LAST_MODIFY_TIMESTAMP = "appLastModifyTimestamp";


        public final Set<PluginMeta> metas;
        public final long lastModifyTimestamp;

        public final File appDir;

        public PluginMetas(File appDir, Set<PluginMeta> metas, long lastModifyTimestamp) {
            this.metas = metas;
            this.lastModifyTimestamp = lastModifyTimestamp;
            this.appDir = appDir;
        }
    }

    private static File getSubPathDir(Key appKey) {
        return new File(TIS.pluginCfgRoot, appKey.getSubDirPath());
    }

    public KeyedPluginStore(Key key, IPluginProcessCallback<T>... pluginCreateCallback) {
        super(key.pluginClass.getClazz(), key.getSotreFile(), pluginCreateCallback);
        this.key = key;
    }

    public interface IPluginKeyAware {
        public void setKey(Key key);
    }

    @Override
    public List<T> getPlugins() {
        List<T> plugins = super.getPlugins();
        for (T plugin : plugins) {
            if (plugin instanceof IPluginKeyAware) {
                ((IPluginKeyAware) plugin).setKey(this.key);
            }
        }
        return plugins;
    }

    @Override
    public synchronized SetPluginsResult setPlugins(IPluginContext pluginContext, Optional<Context> context,
                                                    List<Descriptor.ParseDescribable<T>> dlist, boolean update) {
        SetPluginsResult updateResult = super.setPlugins(pluginContext, context, dlist, update);
        if (updateResult.success && updateResult.cfgChanged) {
            // 本地写入时间戳文件，以备分布式文件同步之用
            updateResult.lastModifyTimeStamp = writeLastModifyTimeStamp(getLastModifyToken(this.key));
        }
        return updateResult;
    }

    @Override
    public File getLastModifyTimeStampFile() {
        return new File(getSubPathDir(this.key), CenterResource.KEY_LAST_MODIFIED_EXTENDION);
    }

    @Override
    protected String getSerializeFileName() {
        return key.getSerializeFileName();
    }

    public static class Key<T extends Describable> {

        public final KeyVal keyVal;
        protected final String groupName;
        private final boolean metaCfgDir;
        public final PluginClassCategory<T> pluginClass;

        public Key(String groupName, String keyVal, Class<T> pluginClass) {
            this(groupName, new KeyVal(keyVal), pluginClass, false);
        }

        public Key(String groupName, KeyVal keyVal, Class<T> pluginClass) {
            this(groupName, keyVal, pluginClass, false);
        }

        public Key(String groupName, KeyVal keyVal, Class<T> pluginClass, boolean metaCfgDir) {
            this(groupName, keyVal, new PluginClassCategory(pluginClass), metaCfgDir);
        }

        public Key(String groupName, KeyVal keyVal, PluginClassCategory<T> pluginClass, boolean metaCfgDir) {
            Objects.requireNonNull(keyVal, "keyVal can not be null");
            this.keyVal = keyVal;
            this.pluginClass = pluginClass;
            this.groupName = groupName;
            this.metaCfgDir = metaCfgDir;
        }

        public String getSerializeFileName() {
            return this.getSubDirPath() + File.separator + pluginClass.getName();
        }

        public final String getSubDirPath() {
            return groupName + File.separator + keyVal.getKeyVal();
        }

        public XmlFile getSotreFile() {
            return Descriptor.getConfigFile(getSerializeFileName(), this.metaCfgDir);
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Key key = (Key) o;
            return this.hashCode() == key.hashCode();
        }

        @Override
        public int hashCode() {
            return Objects.hash(keyVal.getKeyVal(), pluginClass.hashCode());
        }
    }

    public static class PluginClassCategory<T> {
        private final Class<T> pluginClass;
        private final Optional<String> suffix;

        public PluginClassCategory(Class<T> pluginClass) {
            this(pluginClass, Optional.empty());
        }

        public PluginClassCategory(Class<T> pluginClass, String suffix) {
            this(pluginClass, Optional.of(suffix));
        }

        private PluginClassCategory(Class<T> pluginClass, Optional<String> suffix) {
            this.pluginClass = pluginClass;
            this.suffix = suffix;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PluginClassCategory<?> that = (PluginClassCategory<?>) o;
            return Objects.equals(this.hashCode(), that.hashCode());
        }

        @Override
        public int hashCode() {
            if (suffix.isPresent()) {
                return Objects.hash(pluginClass, suffix.get());
            } else {
                return Objects.hash(pluginClass);
            }
        }

        public String getName() {
            return pluginClass.getName() + suffix.orElse(StringUtils.EMPTY);
        }

        public Class<T> getClazz() {
            return this.pluginClass;
        }
    }

    public static class KeyVal {
        private final String val;
        protected final String suffix;


        public KeyVal(String val, String suffix) {
            if (StringUtils.isEmpty(val)) {
                throw new IllegalArgumentException("param 'key' can not be null");
            }
            this.val = val;
            this.suffix = suffix;
        }

        public KeyVal(String val) {
            this(val, StringUtils.EMPTY);
        }

        @Override
        public String toString() {
            return getKeyVal();
        }

        public String getKeyVal() {
            return StringUtils.isBlank(this.suffix) ? getVal() : TMP_DIR_NAME + (getVal() + "-" + this.suffix);
        }


        public String getVal() {
            return val;
        }

        public String getSuffix() {
            return suffix;
        }
    }

    public static class AppKey<TT extends Describable> extends Key<TT> {
        public final StoreResourceType resourceType;

        public AppKey(IPluginContext pluginContext, StoreResourceType resourceType, String appname, Class<TT> clazz) {
            this(pluginContext, resourceType, appname, new PluginClassCategory(clazz));
        }

        public AppKey(IPluginContext pluginContext, StoreResourceType resourceType, String appname,
                      PluginClassCategory<TT> clazz) {
            super(resourceType.getType(), calAppName(pluginContext, appname), clazz, resourceType.useMetaCfgDir);
            this.resourceType = resourceType;
        }


        public boolean isDB() {
            return this.resourceType == StoreResourceType.DataBase;
        }

        public static KeyVal calAppName(IPluginContext pluginContext, String appname) {
            if (pluginContext == null) {
                return new KeyVal(appname);
            }
            String referer = StringUtils.trimToEmpty(pluginContext.getRequestHeader(DataxReader.HEAD_KEY_REFERER));
            Matcher configPathMatcher = DATAX_UPDATE_PATH.matcher(referer);
            boolean inUpdateProcess = configPathMatcher.find();
            if (inUpdateProcess && !pluginContext.isCollectionAware()) {
                throw new IllegalStateException("pluginContext.isCollectionAware() must be true");
            }
            return (pluginContext != null && inUpdateProcess) ? new KeyVal(appname, pluginContext.getExecId()) :
                    new KeyVal(appname);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(keyVal.getKeyVal(), resourceType.getType(), pluginClass);
        }
    }
}
