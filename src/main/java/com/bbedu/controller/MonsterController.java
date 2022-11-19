package com.bbedu.controller;

import com.bbedu.bbspringmvc.annotation.AutoWired;
import com.bbedu.bbspringmvc.annotation.Controller;
import com.bbedu.bbspringmvc.annotation.RequestMapping;
import com.bbedu.entity.Monster;
import com.bbedu.service.MonsterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@Controller
public class MonsterController {

    @AutoWired
    private MonsterService monsterService;

    /**
     * 编写方法可以列出 Monster 列表
     * 直接使用原生servlet
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

/*
    @RequestMapping(value = "/monster/test")
    public void test() {

    }*/
}
