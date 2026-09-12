package cn.edu.czjtxy.sjypeploemanage.service;

import cn.edu.czjtxy.sjypeploemanage.entity.Character;

import java.util.List;

public interface CharacterService {

    List<Character> list(Long userId);

    Character getById(Long id);

    void add(Character character);

    void update(Character character);

    void adjust(Long id, Integer wisdom, Integer force, Integer social, Integer agility);

    void delete(Long id);
}