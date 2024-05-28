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
package com.qlangtech.tis.extension.util;

import com.qlangtech.tis.trigger.util.JsonUtil;
import com.qlangtech.tis.util.UploadPluginMeta;
import groovy.lang.Script;

import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * @author 百岁（baisui@qlangtech.com）
 * @date 2021-02-06 13:27
 */
public class GroovyShellEvaluate {
    static final boolean isInConsoleModule;

    static {
        boolean loaded = false;
        try {
            loaded = (null != Class.forName("com.qlangtech.tis.runtime.module.action.BasicModule"));
        } catch (ClassNotFoundException e) {
        }
        isInConsoleModule = loaded;
    }
//    private static GroovyShellEvaluateService getEvaluateService(){
//
//        ServiceLoader<GroovyShellEvaluateService> evaluateServiceLoader = ServiceLoader.load(GroovyShellEvaluateService.class);
//
//        for(GroovyShellEvaluateService eservie : evaluateServiceLoader){
//              return eservie;
//        }
//        return new GroovyShellEvaluateService() {
//            @Override
//            public <T> T createParamizerScript(Class parentClazz, String className, String script) {
//                return null;
//            }
//
//            @Override
//            public Object scriptEval(String script, Function<Object, Object>... process) {
//                return null;
//            }
//
//            @Override
//            public <T> T eval(String javaScript) {
//                return null;
//            }
//        };
//    }

    public static <T> T createParamizerScript(Class parentClazz, String className, String script) {
        try {
            String pkg = parentClazz.getPackage().getName();
            GroovyShellUtil.loadMyClass(className, script);
            Class<?> groovyClass = GroovyShellUtil.loadClass(pkg, className);
            return (T) groovyClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object scriptEval(String script, Function<Object, Object>... process) {
        try {
            UploadPluginMeta meta = UploadPluginMeta.parse(script, true);
            boolean unCache = meta.getBoolean(UploadPluginMeta.KEY_UNCACHE);

            Callable<Object> valGetter = () -> {
                for (Function<Object, Object> f : process) {
                    Object val = eval(meta.getName());
                    if (val == null) {
                        return null;
                    }
                    return f.apply(val);
                }
                return eval(meta.getName());
            };
            return unCache ? new JsonUtil.UnCacheString(valGetter) : valGetter.call();
        } catch (Exception e) {
            throw new RuntimeException("script:" + script, e);
        }
    }


    private GroovyShellEvaluate() {
    }

    public static <T> T eval(String javaScript) {
        if (!GroovyShellUtil.getGroovyShellFactory().isInConsoleModule()) {
            // 如果不在console中运行则返回空即可
            return null;
        }
        try {
            Script script = GroovyShellUtil.getScriptCache().get(javaScript);
            return (T) script.run();
        } catch (Throwable e) {
            throw new RuntimeException(javaScript, e);
        }
    }
}
