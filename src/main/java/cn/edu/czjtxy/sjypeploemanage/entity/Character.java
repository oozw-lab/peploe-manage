package cn.edu.czjtxy.sjypeploemanage.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Character {
    private Long id;
    private String name;
    private String gender;
    private Integer wisdom;
    private Integer force;
    private Integer social;
    private Integer agility;
    private Integer status;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}