package com.bbedu.bbspringmvc.servlet;

import com.bbedu.bbspringmvc.annotation.Controller;
import com.bbedu.bbspringmvc.annotation.RequestMapping;
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
     * 分发请求
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
                bbHandler.getMethod().invoke(bbHandler.getController(), request, response);
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
