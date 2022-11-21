package com.bbedu.service.impl;

import com.bbedu.bbspringmvc.annotation.Service;
import com.bbedu.entity.Monster;
import com.bbedu.service.MonsterService;

import java.util.ArrayList;

/**
 * MonsterServiceImpl 作为 service 注入 spring
 * @author BBChen
 */
@Service(value = "monsterService")
public class MonsterServiceImpl implements MonsterService {

    // 模拟 DAO 层, 以后用 MyBatis 接管
    @Override
    public ArrayList<Monster> getMonsterList() {

        ArrayList<Monster> monsters = new ArrayList<>();

        monsters.add(new Monster(111, "牛魔王", "蛮牛冲撞", 400));
        monsters.add(new Monster(222, "铁扇公主", "芭蕉扇", 600));

        return monsters;
    }

    @Override
    public ArrayList<Monster> findMonsterByName(String name) {

        ArrayList<Monster> monsters =
                new ArrayList<>();

        monsters.add(new Monster(111, "牛魔王", "蛮牛冲撞", 400));
        monsters.add(new Monster(222, "铁扇公主", "芭蕉扇", 600));
        monsters.add(new Monster(333, "大象精", "运木头", 100));
        monsters.add(new Monster(444, "黄袍怪", "吹沙子", 200));
        monsters.add(new Monster(555, "黑熊精", "偷袈裟", 300));
        monsters.add(new Monster(666, "老鼠精", "偷油吃", 500));

        ArrayList<Monster> findMonsters = new ArrayList<>();

        for (Monster monster : monsters) {
            if (monster.getName().contains(name)) {
                findMonsters.add(monster);
            }
        }

        return findMonsters;
    }

    @Override
    public boolean login(String name) {

        // 实际是到 DB 验证 -> 此处模拟
        if ("牛魔王".equals(name)) {
            return true;
        } else {
            return false;
        }
    }
}
