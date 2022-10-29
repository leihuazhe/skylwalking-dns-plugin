/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.plugn.dns.define;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.StaticMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassStaticMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.named;


public class DnsInstrumentation extends ClassStaticMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "java.net.InetAddress";
    private static final Map<String, String> ENHANCE_METHODS = new HashMap<>();

    static {
        ENHANCE_METHODS.put("getAllByName", "org.apache.skywalking.apm.plugn.dns.DnsInterceptor");
    }

    @Override
    public StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints() {
        final List<StaticMethodsInterceptPoint> points = new ArrayList<>(ENHANCE_METHODS.size());

        for (Map.Entry<String, String> entry : ENHANCE_METHODS.entrySet()) {
            final StaticMethodsInterceptPoint point = new StaticMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named(entry.getKey()).and(ElementMatchers.takesArguments(1));
                }

                @Override
                public String getMethodsInterceptor() {
                    return entry.getValue();
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            };
            points.add(point);
        }
        return points.toArray(new StaticMethodsInterceptPoint[0]);
    }


    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }

    @Override
    public boolean isBootstrapInstrumentation() {
        return true;
    }
}