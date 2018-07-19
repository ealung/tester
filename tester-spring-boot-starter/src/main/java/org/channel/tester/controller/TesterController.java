package org.channel.tester.controller;

import org.channel.tester.utils.ObjectToMap;
import org.channel.tester.utils.TestUtils;
import org.channel.tester.vo.WebTestParam;
import org.channel.tester.vo.WebTestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchanglu
 * @since 2018/05/11 17:08.
 */
@Controller
@Slf4j
public class TesterController implements ApplicationListener<ContextRefreshedEvent> {
    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    private List<WebTestVO> webTestVOS = null;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            initMapping();
        } catch (Exception e) {
            log.warn("tester init fail", e);
        }
    }

    @RequestMapping(value = "mapping", method = RequestMethod.GET)
    @ResponseBody
    public List<WebTestVO> mapping(String mappingName, Integer mapping) {
        if (null == mappingName || mappingName.trim().equals("")) {
            return webTestVOS;
        }
        return webTestVOS.stream()
                .filter(webTestVO -> {
                    if (mapping == 1) {
                        return webTestVO.getBeanName().toUpperCase().contains(mappingName.toUpperCase());
                    } else {
                        return webTestVO.getUrl().toUpperCase().contains(mappingName.toUpperCase());
                    }
                })
                .collect(Collectors.toList());
    }

    public void initMapping() {
        webTestVOS = new ArrayList<>();
        Set<Map.Entry<RequestMappingInfo, HandlerMethod>> entries = requestMappingHandlerMapping.getHandlerMethods().entrySet();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> requestMappingInfoHandlerMethodEntry : entries) {
            RequestMappingInfo key = requestMappingInfoHandlerMethodEntry.getKey();
            HandlerMethod value = requestMappingInfoHandlerMethodEntry.getValue();
            if (log.isDebugEnabled()) {
                log.debug("RequestMappingInfo:{}", key);
                log.debug("HandlerMethod:{}", value);
            }
            List<List<WebTestParam>> objects = mockParameter(value.getMethod());
            boolean jsonParameter = isJsonParameter(value.getMethod().getParameterAnnotations());
            boolean jsonReturn = isJsonReturn(value);
            Set<String> patterns = key.getPatternsCondition().getPatterns();
            for (String pattern : patterns) {
                WebTestVO webTestVO = WebTestVO.builder()
                        .beanName(requestMappingInfoHandlerMethodEntry.getValue().getBeanType().getName())
                        .url(pattern)
                        .requestMethod(key.getMethodsCondition().getMethods())
                        .parameter(objects)
                        .jsonParameter(jsonParameter)
                        .jsonReturn(jsonReturn)
                        .build();
                webTestVOS.add(webTestVO);
            }
        }
    }

    private boolean isJsonParameter(Annotation[][] parameterAnnotations) {
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation annotation : parameterAnnotation) {
                if (isRequestBodyAnnotation(annotation)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isJsonReturn(HandlerMethod value) {
        RestController declaredAnnotation1 = value.getBeanType().getDeclaredAnnotation(RestController.class);
        ResponseBody declaredAnnotation = value.getMethod().getDeclaredAnnotation(ResponseBody.class);
        return null != declaredAnnotation1 || null != declaredAnnotation;
    }

    private boolean isRequestBodyAnnotation(Annotation annotation) {
        return annotation.annotationType().getTypeName().equals("org.springframework.web.bind.annotation.RequestBody");
    }

    private List<List<WebTestParam>> mockParameter(Method method) {
        if (log.isDebugEnabled()) {
            log.debug("method:{}", method);
        }
        ParameterNameDiscoverer parameterNameDiscoverer =
                new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();
        List<List<WebTestParam>> parameterList = new ArrayList<>(parameterTypes.length);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.getType().isInterface()) {
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug("parameter:{}", parameter);
            }
            Map<String, Object> map = new HashMap<>();
            Object randomTest = TestUtils.getRandomTest(parameter.getType());
            if (null != randomTest) {
                map.put(parameterNames[i], randomTest);
            } else {
                //map.put(parameter.getName(),ObjectToMap.toMap(TestUtils.getTestObject(parameter.getType())));
                map = ObjectToMap.toMap(TestUtils.getTestObject(parameter.getType()));
            }
            if (null != map) {
                parameterList.add(convertToWebTestParam(map));
            }
        }
        return parameterList;
    }

    private List<WebTestParam> convertToWebTestParam(Map<String, Object> map) {
        List<WebTestParam> list = new ArrayList<>();
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            list.add(new WebTestParam(stringObjectEntry.getKey(), stringObjectEntry.getValue()));
        }
        return list;
    }
}
