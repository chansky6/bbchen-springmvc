package com.bbedu.service.impl;

import com.bbedu.bbspringmvc.annotation.Service;
import com.bbedu.entity.Monster;
import com.bbedu.service.MonsterService;

import java.util.ArrayList;

/**
 * MonsterServiceImpl 作为 service 注入 spring
 */
@Service/*(value = "myService")*/
public class MonsterServiceImpl implements MonsterService {

    // 模拟 DAO 层, 以后用 MyBatis 接管
    @Override
    public ArrayList<Monster> getMonsterList() {

        ArrayList<Monster> monsters = new ArrayList<>();

        monsters.add(new Monster(111, "牛魔王", "蛮牛冲撞", 400));
        monsters.add(new Monster(222, "铁扇公主", "芭蕉扇", 600));

        return monsters;
    }
}
