package cn.edu.czjtxy.sjypeploemanage.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Event {
    private Long id;
    private String name;
    private String description;
    private Integer category;
    private Integer status;
    private Integer wisdomBonus;
    private Integer forceBonus;
    private Integer socialBonus;
    private Integer agilityBonus;
    private Long characterId;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}