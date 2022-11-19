package com.bbedu.entity;

/**
 * @projectName: bb-springmvc
 * @package: com.bbedu.entity
 * @className: Monster
 * @author: BBChen
 * @description: TODO
 * @date: 2022/11/19 16:09
 * @version: 1.0
 */
public class Monster {
    private Integer id;
    private String name;
    private String skill;
    private Integer age;

    public Monster(Integer id, String name, String skill, Integer age) {
        this.id = id;
        this.name = name;
        this.skill = skill;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Monster{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", skill='" + skill + '\'' +
                ", age=" + age +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
