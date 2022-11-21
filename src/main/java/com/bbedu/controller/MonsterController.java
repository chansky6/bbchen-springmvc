package com.bbedu.controller;

import com.bbedu.bbspringmvc.annotation.AutoWired;
import com.bbedu.bbspringmvc.annotation.Controller;
import com.bbedu.bbspringmvc.annotation.RequestMapping;
import com.bbedu.bbspringmvc.annotation.RequestParam;
import com.bbedu.entity.Monster;
import com.bbedu.service.MonsterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author BBChen
 */
@Controller
public class MonsterController {

    @AutoWired
    private MonsterService monsterService;

    /**
     * 编写方法可以列出 Monster 列表
     * 直接使用原生servlet
     * @param request
     * @param response
     */
    @RequestMapping(value = "/monster/list")
    public void listMonsters(HttpServletRequest request, HttpServletResponse response) {
        // 设置返回类型
        response.setContentType("text/html;charset=utf-8");

        StringBuilder content = new StringBuilder("<h1>妖怪列表信息</h1>");

        ArrayList<Monster> monsterList = monsterService.getMonsterList();
        content.append("<table>");
        content.append("<table border='1px' width='500px' style='border-collapse:collapse'>");
        for (Monster monster : monsterList) {
            content.append("<tr><td>" + monster.getId()
                    +"</td><td>" + monster.getName()
                    +"</td><td>" + monster.getSkill()
                    +"</td><td>" + monster.getAge() + "</td></tr>");
        }
        content.append("</table>");

        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据名字查找 Monster
     * @param request
     * @param response
     * @param name
     */
    @RequestMapping(value = "/monster/find")
    public void findMonsterByName(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String name) {
        // 设置返回类型
        response.setContentType("text/html;charset=utf-8");

        System.out.println("---接收到的name---" + name);
        StringBuilder content = new StringBuilder("<h1>妖怪列表信息</h1>");

        ArrayList<Monster> monsterList = monsterService.findMonsterByName(name);
        content.append("<table>");
        content.append("<table border='1px' width='500px' style='border-collapse:collapse'>");
        for (Monster monster : monsterList) {
            content.append("<tr><td>" + monster.getId()
                    +"</td><td>" + monster.getName()
                    +"</td><td>" + monster.getSkill()
                    +"</td><td>" + monster.getAge() + "</td></tr>");
        }
        content.append("</table>");

        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理登录方法
     * @param monsterName
     * @return
     */
    @RequestMapping(value = "/monster/login")
    public String login(HttpServletRequest request,
                        HttpServletResponse response,
                        String monsterName) {

        System.out.println("---接收到monsterName---" + monsterName);
        boolean login = monsterService.login(monsterName);

        // 将 monsterName 设置到 request 域
        request.setAttribute("monsterName", monsterName);
        if (login) {
            // 登录成功
//            return "forward:/login_ok.jsp";
            // 测试重定向
//            return "redirect:/bb-springmvc/login_ok.jsp";
            return "/login_ok.jsp";
        } else {
            // 登录失败
            return "forward:/login_error.jsp";
        }
    }


/*
    @RequestMapping(value = "/monster/test")
    public void test() {

    }*/
}
