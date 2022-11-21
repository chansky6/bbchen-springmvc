package com.bbedu.bbspringmvc.servlet;

import com.bbedu.bbspringmvc.annotation.Controller;
import com.bbedu.bbspringmvc.annotation.RequestMapping;
import com.bbedu.bbspringmvc.annotation.RequestParam;
import com.bbedu.bbspringmvc.context.BBWebApplicationContext;
import com.bbedu.bbspringmvc.handler.BBHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 充当原生DispatcherServlet
 * 本质是Servlet
 * @author BBChen
 */
public class BBDispatcherServlet extends HttpServlet {

    // 定义属性 handlerList, 保存 BBHandler
    private List<BBHandler> bbHandlerList =
            new ArrayList<>();

    // 自己的 Spring 容器
    BBWebApplicationContext bbWebApplicationContext = null;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        /*
            GenericServlet' init(ServletConfig config) javadoc:
            This implementation stores the ServletConfig object it receives from the servlet container for later use.
            When overriding this form of the method, call super.init(config).
         */
        super.init(servletConfig);

        // 获取 web.xml 中的 contextConfigLocation
        String configLocation =
                servletConfig.getInitParameter("contextConfigLocation");

        bbWebApplicationContext =
                new BBWebApplicationContext(configLocation);

        bbWebApplicationContext.init();

        // 调用 initHandlerMapping，完成 URL 和控制器方法的映射
        initHandlerMapping();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        System.out.println("--BBDispatcherServlet--doPost--");
        // 调用方法，完成请求分发
        executeDispatch(req, resp);
    }

    /**
     * 完成URL和控制器方法的映射
     */
    private void initHandlerMapping() {
        ConcurrentHashMap<String, Object> ioc = bbWebApplicationContext.ioc;
        if (ioc.isEmpty()) {
            // 容器判空
            return;
        }

        // 遍历 ioc 的 bean对象，然后进行 URL 映射处理
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 取出 Object
            Class<?> clazz = entry.getValue().getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
                // 带有 @Controller 注解
                Method[] declaredMethods = clazz.getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    if (declaredMethod.isAnnotationPresent(RequestMapping.class)) {
                        // 方法带有 @RequestMapping 注解
                        RequestMapping requestMappingAnnotation =
                                declaredMethod.getAnnotation(RequestMapping.class);
                        String url = getServletContext().getContextPath() + requestMappingAnnotation.value();
                        BBHandler bbHandler = new BBHandler(url, entry.getValue(), declaredMethod);
                        bbHandlerList.add(bbHandler);
                    }
                }
            }
        }
        System.out.println("bbHandlerList = " + bbHandlerList);
    }


    /**
     * 通过 request 对象，返回 BBHandler 对象
     * @param request HttpServletRequest
     * @return
     */
    private BBHandler getBBHandler(HttpServletRequest request) {

        // 获取用户请求的 URI
        String requestURI = request.getRequestURI();
        for (BBHandler bbHandler : bbHandlerList) {
            if (requestURI.equals(bbHandler.getUrl())) {
                // 匹配成功
                return bbHandler;
            }
        }
        return null;
    }

    /**
     * 完成分发请求任务
     * @param request
     * @param response
     */
    private void executeDispatch(HttpServletRequest request,
                                 HttpServletResponse response) {
        BBHandler bbHandler = getBBHandler(request);
        try {
            if (bbHandler == null) {
                response.getWriter().print("<h1>404 NOT FOUND</h1>");
            } else {
                /*
                    下面的写法，是针对目标方法是形如：
                    method(HttpServletRequest request, HttpServletResponse response)
                 */
//                bbHandler.getMethod()
//                        .invoke(bbHandler.getController(), request, response);

                // 将需要传递给目标方法的 实参 封装到参数数组，=> 反射调用
                // public Object invoke(Object obj, Object... args)

                // 1.得到目标方法的参数信息
                Class<?>[] parameterTypes =
                        bbHandler.getMethod().getParameterTypes();

                // 2.创建参数数组(对应实参数组), 在后面反射调用目标方法时, 会使用到
                Object[] params = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> parameterType = parameterTypes[i];

                    if ("HttpServletRequest".equals(parameterType.getSimpleName())) {
                        params[i] = request;
                    } else if ("HttpServletResponse".equals(parameterType.getSimpleName())) {
                        params[i] = response;
                    }
                }

                // 获得 Http 请求的参数集合
                Map<String, String[]> parameterMap =
                        request.getParameterMap();

                // 遍历
                // Map.Entry<String, String[]>
                // 第一个参数 String 表示请求参数名
                // 第二个参数 String[] 表示请求值
                for (Map.Entry<String, String[]> stringEntry : parameterMap.entrySet()) {
                    // 取出 key => 请求参数名
                    String name = stringEntry.getKey();
                    // 仅考虑单个值
                    String value = stringEntry.getValue()[0];

                    // 对应目标方法的第几个参数
                    // 得到请求参数对应的是第几个形参 => 单独编写方法
//                    params[?]
                    int paramIndex = getIndexRequestParamIndex(bbHandler.getMethod(), name);
                    if (paramIndex != -1) {
                        // 找到对应位置
                        params[paramIndex] = value;
                    } else {
                        // TODO 没有找到，使用默认机制
                        // 得到目标方法所有形参名称 => 单独编写方法
                        // 对得到目标方法的所有形参名进行匹配 匹配 => 填充到 params
                        List<String> parameterNames =
                                getParameterNames(bbHandler.getMethod());

                        for (int i = 0; i < parameterNames.size(); i++) {
                            // 如果匹配
                            if (name.equals(parameterNames.get(i))) {
                                params[i] = value;
                                break;
                            }
                        }
                    }

                }

                bbHandler.getMethod()
                        .invoke(bbHandler.getController(), params);
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 编写一个方法，返回得到请求参数在对应方法是第几个形参
     * @param method
     * @param name
     * @return int
     */
    public int getIndexRequestParamIndex(Method method, String name) {

        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            boolean annotationPresent = parameter.isAnnotationPresent(RequestParam.class);
            if (annotationPresent) {
                // 有 @RequestParam
                RequestParam requestParamAnnotation = parameter.getAnnotation(RequestParam.class);
                String value = requestParamAnnotation.value();
                // 匹配比较
                if (name.equals(value)) {
                    return i;
                }
            }
        }

        // 没有匹配成功
        return -1;
    }

    /**
     * 得到目标方法所有形参名称
     * @param method
     * @return List<String>
     */
    public List<String> getParameterNames(Method method) {

        List<String> parametersList = new ArrayList<>();

        // 获取所有参数名称 -> 细节
        // 默认情况下，parameter.getName() 得到的名字，不是形参真正的名字
        // 而是 [arg0, arg1, arg2...]
        // !!! 引入插件, 使用java8特性
        Parameter[] parameters = method.getParameters();

        for (Parameter parameter : parameters) {
            parametersList.add(parameter.getName());
        }
        System.out.println("parametersList = " + parametersList);
        return parametersList;
    }
}
