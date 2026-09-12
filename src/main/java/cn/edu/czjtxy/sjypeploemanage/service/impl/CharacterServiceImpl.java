package cn.edu.czjtxy.sjypeploemanage.service.impl;

import cn.edu.czjtxy.sjypeploemanage.entity.Character;
import cn.edu.czjtxy.sjypeploemanage.mapper.CharacterMapper;
import cn.edu.czjtxy.sjypeploemanage.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CharacterServiceImpl implements CharacterService {

    @Autowired
    private CharacterMapper characterMapper;

    @Override
    public List<Character> list(Long userId) {
        return characterMapper.selectList(userId);
    }

    @Override
    public Character getById(Long id) {
        Character character = characterMapper.selectById(id);
        if (character == null) {
            throw new RuntimeException("角色不存在");
        }
        return character;
    }

    @Override
    public void add(Character character) {
        if (character.getWisdom() == null) character.setWisdom(0);
        if (character.getForce() == null) character.setForce(0);
        if (character.getSocial() == null) character.setSocial(0);
        if (character.getAgility() == null) character.setAgility(0);
        characterMapper.insert(character);
    }

    @Override
    @Transactional
    public void update(Character character) {
        Character exist = characterMapper.selectById(character.getId());
        if (exist == null) {
            throw new RuntimeException("角色不存在");
        }
        characterMapper.update(character);
    }

    @Override
    @Transactional
    public void adjust(Long id, Integer wisdom, Integer force, Integer social, Integer agility) {
        Character exist = characterMapper.selectById(id);
        if (exist == null) {
            throw new RuntimeException("角色不存在");
        }
        characterMapper.adjust(id, wisdom, force, social, agility);
    }

    @Override
    public void delete(Long id) {
        characterMapper.deleteById(id);
    }
}