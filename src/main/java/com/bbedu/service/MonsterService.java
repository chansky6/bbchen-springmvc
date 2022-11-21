package com.bbedu.service;

import com.bbedu.entity.Monster;

import java.util.ArrayList;

public interface MonsterService {
    /**
     * 返回 Monster 列表
     * @return
     */
    ArrayList<Monster> getMonsterList();

    /**
     * 通过传入的name,返回 Monster 列表
     * @param name
     * @return
     */
    ArrayList<Monster> findMonsterByName(String name);

    /**
     * 处理登录
     * @param name
     * @return
     */
    public boolean login(String name);
}
