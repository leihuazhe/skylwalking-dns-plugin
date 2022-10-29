package org.apache.skywalking.apm.plugn.dns;

import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.StaticMethodsAroundInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * DnsInterceptor
 *
 * @author leihz
 * @version 1.0.0
 * @since 2022/10/29 22:26
 */
public class DnsInterceptor implements StaticMethodsAroundInterceptor {

    private static final ILog LOGGER = LogManager.getLogger(DnsInterceptor.class);

    private static final List<String> RULE_LIST = Arrays.asList("www.baidu.com");

    @Override
    public void beforeMethod(Class clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes, MethodInterceptResult result) {
        String methodName = method.getName();
        LOGGER.info("Intercept unknown method: {}, arguments:{}.", method, Arrays.toString(allArguments));
        enhance((String) allArguments[0], methodName);
    }

    @Override
    public Object afterMethod(Class clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes, Object ret) {
        return ret;
    }

    @Override
    public void handleMethodException(Class clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes, Throwable t) {

    }

    private void enhance(String host, String method) {
        if (RULE_LIST == null || host == null) {
            return;
        }
        for (String rule : RULE_LIST) {
            if (rule.equals(host)) {
                LOGGER.info("Reject dns {} query: {}, rule:{}.", method, host, rule);
                throw new RuntimeException();
            }
        }
    }
}