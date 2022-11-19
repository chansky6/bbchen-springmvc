package com.bbedu.controller;

import com.bbedu.bbspringmvc.annotation.Controller;
import com.bbedu.bbspringmvc.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class OrderController {
    /**
     * 编写方法可以列出 Monster 列表
     * 直接使用原生servlet
     */
    @RequestMapping(value = "/order/list")
    public void listOrder(HttpServletRequest request, HttpServletResponse response) {
        // 设置返回类型
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write("<h1>Order信息列表</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/order/add")
    public void addOrder(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write("<h1>添加订单...</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
