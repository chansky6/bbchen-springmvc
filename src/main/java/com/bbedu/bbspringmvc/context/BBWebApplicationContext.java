package com.bbedu.bbspringmvc.context;

import com.bbedu.bbspringmvc.annotation.AutoWired;
import com.bbedu.bbspringmvc.annotation.Controller;
import com.bbedu.bbspringmvc.annotation.Service;
import com.bbedu.bbspringmvc.xml.XMLParser;
import org.apache.commons.lang3.StringUtils;

import java.beans.beancontext.BeanContext;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BBWebApplicationContext {
    // 定义属性, 保存扫描包/子包的类的全路径
    private List<String> classFullPathList =
            new ArrayList<>();

    // 定义属性，存放反射生成的 bean 对象 /Controller/Service
    public ConcurrentHashMap<String, Object> ioc =
            new ConcurrentHashMap<>();

    public BBWebApplicationContext() {
    }

    private String configLocation = "";

    public BBWebApplicationContext(String configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * 完成自己的 spring 容器初始化
     */
    public void init() {

//        String basePackage = XMLParser.getBasePackage("bbspringmvc.xml");
        String basePackage =
                XMLParser.getBasePackage(configLocation.split(":")[1]);
        String[] basePackages = basePackage.split(",");
        if (basePackages.length > 0) {
            for (String aPackage : basePackages) {
                scanPackage(aPackage);
            }
        }
        System.out.println("扫描后的 classFullPathList = " + classFullPathList);
        // 完成类的反射注入
        executeInstance();
        System.out.println("扫描后的 ioc容器 = " + ioc);
        // 完成注入 bean 对象的属性的自动装配
        executeAutoWired();
        System.out.println("自动装配后的 ioc容器 = " + ioc);
    }

    /**
     * 创建方法，完成对包的扫描
     * 启动tomcat来测试
     * @param pack
     */
    public void scanPackage(String pack) {

        // 得到包所在的工作路径(绝对路径)
        URL url = this.getClass()
                .getClassLoader()
                .getResource("/" + pack.replaceAll("\\.", "/"));

//        System.out.println("url = " + url);

        // 根据得到的路径，对其进行扫描，把类的全路径，保存到 classFullPathList
        String path = url.getFile();
        System.out.println("path = " + path);
        File dir = new File(path);
        // 遍历子目录
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                // 递归扫描
                scanPackage(pack + "." + file.getName());
            } else {
                // 可能是.class 也可能是其他
                String classFullPath =
                        pack + "." + file.getName().replaceAll(".class", "");
                classFullPathList.add(classFullPath);
            }
        }
    }

    /**
     * 将扫描到的类，在满足条件的情况下，反射到 ioc 容器
     */
    public void executeInstance() {
        // 判断是否扫描到类
        if (classFullPathList.size() == 0) {
            return;
        }

        // 遍历 classFullPathList，进行反射
        for (String classFullPath : classFullPathList) {
            try {
                Class<?> clazz = Class.forName(classFullPath);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    // 有 @Controller
                    String beanName = StringUtils.uncapitalize(clazz.getSimpleName());
                    ioc.put(beanName, clazz.newInstance());
                }
                // TODO 可拓展其他注解
                else if (clazz.isAnnotationPresent(Service.class)) {
                    // 有 @Service
                    Service serviceAnnotation = clazz.getAnnotation(Service.class);
                    String beanName = serviceAnnotation.value();

                    if ("".equals(beanName)) {
                        // 未指定 value
                        // 通过 接口名/类名(首字母小写) 注入 ioc
                        // 得到所有接口名
                        Class<?>[] interfaces = clazz.getInterfaces();
                        Object instance = clazz.newInstance();
                        // 通过多个接口名注入
                        for (Class<?> anInterface : interfaces) {
                            String beanName2 =
                                    StringUtils.uncapitalize(anInterface.getSimpleName());
                            ioc.put(beanName2, instance);
                        }

                    } else {
                        ioc.put(StringUtils.uncapitalize(beanName), clazz.newInstance());
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 完成属性的自动装配

    public void executeAutoWired() {
        if (ioc.isEmpty()) {
            throw new RuntimeException("无法完成AutoWired, ioc 容器中没有对象");
        }

        // 遍历ioc
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            String key = entry.getKey();
            Object bean = entry.getValue();

            // 获取 bean 的所有字段
            Class<?> clazz = bean.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(AutoWired.class)) {
                    // 字段含有 @AutoWired
                    AutoWired autoWiredAnnotation = declaredField.getDeclaredAnnotation(AutoWired.class);
                    String beanName = autoWiredAnnotation.value();
                    if ("".equals(beanName)) {
                        // 未设置 value
                        // 通过字段类型的首字母小写，作为名字装配
                        Class<?> type = declaredField.getType();
                        beanName = StringUtils
                                .uncapitalize(type.getSimpleName());
                    } else {
                        // 已设置 value
                        if (ioc.get(beanName) == null) {
                            // 指定 value 对应 bean 不在 ioc 中
                            throw new RuntimeException("ioc 容器中,不存在你要装配的 bean");
                        }
                    }

                    // 注入，并且防止属性private
                    declaredField.setAccessible(true);
                    try {
                        declaredField.set(bean, ioc.get(beanName));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }

    }
}
