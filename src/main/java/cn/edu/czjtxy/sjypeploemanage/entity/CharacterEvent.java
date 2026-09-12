package cn.edu.czjtxy.sjypeploemanage.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CharacterEvent {
    private Long id;
    private Long characterId;
    private Long eventId;
    private LocalDateTime completeTime;
}