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
package com.qlangtech.tis.async.message.client.consumer.impl;

import com.qlangtech.tis.TIS;
import com.qlangtech.tis.annotation.Public;
import com.qlangtech.tis.async.message.client.consumer.IConsumerHandle;
import com.qlangtech.tis.async.message.client.consumer.IMQConsumerStatusFactory;
import com.qlangtech.tis.async.message.client.consumer.IMQListenerFactory;
import com.qlangtech.tis.datax.IDataXPluginMeta;
import com.qlangtech.tis.extension.Describable;
import com.qlangtech.tis.extension.Descriptor;
import com.qlangtech.tis.plugin.IEndTypeGetter;
import com.qlangtech.tis.plugin.IPluginVenderGetter;
import com.qlangtech.tis.plugin.datax.SelectedTabExtend;
import com.qlangtech.tis.plugin.incr.ISelectedTabExtendFactory;
import com.qlangtech.tis.plugin.incr.TISSinkFactory;
import com.qlangtech.tis.util.HeteroEnum;

import java.util.Map;
import java.util.Optional;

/**
 * 监听数据源工厂，类似监听MySql，PG中数据源所对应的Flink SourceFunction 封装
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2020/04/13
 * @see TISSinkFactory
 */
@Public
public abstract class MQListenerFactory
        implements IMQListenerFactory, IMQConsumerStatusFactory, Describable<MQListenerFactory> {

    public static Optional<Descriptor<SelectedTabExtend>> getIncrSourceSelectedTabExtendDescriptor(String dataXName) {
        MQListenerFactory incrSourceFactory = HeteroEnum.getIncrSourceListenerFactory(dataXName);

        Descriptor<MQListenerFactory> descriptor = incrSourceFactory.getDescriptor();
        if (!(descriptor instanceof ISelectedTabExtendFactory)) {
//            throw new IllegalStateException("descriptor:" + descriptor.getClass().getName() + " must be instance of "
//                    + IIncrSourceSelectedTabExtendFactory.class.getName());
            return Optional.empty();
        }
        // Field subFormField, Class instClazz, Descriptor subFormFieldsDescriptor
        Descriptor<SelectedTabExtend> selectedTableExtendDesc
                = ((ISelectedTabExtendFactory) descriptor).getSelectedTableExtendDescriptor();

        return Optional.ofNullable(selectedTableExtendDesc);
//        if (selectedTableExtendDesc == null) {
//           // throw new IllegalStateException("selectedTableExtendDesc can not be null,relevant desc:" + descriptor.getClass().getName());
//        }
//        return Optional.of(selectedTableExtendDesc);
    }

    @Override
    public Descriptor<MQListenerFactory> getDescriptor() {
        Descriptor<MQListenerFactory> descriptor = TIS.get().getDescriptor(this.getClass());
        if (descriptor == null) {
            throw new IllegalStateException("class:" + this.getClass() + " relevant descriptor can not be null");
        }
        Class<BaseDescriptor> expectClass = getExpectDescClass();
        if (!(expectClass.isAssignableFrom(descriptor.getClass()))) {
            throw new IllegalStateException(descriptor.getClass() + " must implement the Descriptor of " + expectClass.getName());
        }
        return descriptor;
    }

    protected <TT extends BaseDescriptor> Class<TT> getExpectDescClass() {
        return (Class<TT>) BaseDescriptor.class;
    }

    public void setConsumerHandle(IConsumerHandle consumerHandle) {
        throw new UnsupportedOperationException();
    }

    public static abstract class BaseDescriptor extends Descriptor<MQListenerFactory> implements IPluginVenderGetter {

        @Override
        public final Map<String, Object> getExtractProps() {
            Map<String, Object> eprops = super.getExtractProps();
            Optional<IEndTypeGetter.EndType> targetType = this.getTargetType();
            //  eprops.put(KEY_END_TYPE, this.getEndType());

            //this.getEndType().appendProps(eprops);

            eprops.put(IDataXPluginMeta.END_TARGET_TYPE, targetType.isPresent() ? targetType.get().getVal() : "all");
            eprops.put(ISelectedTabExtendFactory.KEY_EXTEND_SELECTED_TAB_PROP
                    , (this instanceof ISelectedTabExtendFactory) && (((ISelectedTabExtendFactory) this).getSelectedTableExtendDescriptor() != null));
            return eprops;
        }

        /**
         * 取得服务对象，如果这个Plugin是MySqlCDC的话,则返回 EndType.MySQL, 如果全部匹配的话，则返回empty
         *
         * @return
         */
        public Optional<EndType> getTargetType() {
            return Optional.of(this.getEndType());
        }
    }
}
