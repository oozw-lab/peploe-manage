package cn.edu.czjtxy.sjypeploemanage.service.impl;

import cn.edu.czjtxy.sjypeploemanage.entity.Character;
import cn.edu.czjtxy.sjypeploemanage.entity.CharacterEvent;
import cn.edu.czjtxy.sjypeploemanage.entity.Event;
import cn.edu.czjtxy.sjypeploemanage.mapper.CharacterEventMapper;
import cn.edu.czjtxy.sjypeploemanage.mapper.CharacterMapper;
import cn.edu.czjtxy.sjypeploemanage.mapper.EventMapper;
import cn.edu.czjtxy.sjypeploemanage.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private CharacterMapper characterMapper;

    @Autowired
    private CharacterEventMapper characterEventMapper;

    @Override
    public List<Event> list(Long userId) {
        return eventMapper.selectList(userId);
    }

    @Override
    public Event getById(Long id) {
        Event event = eventMapper.selectById(id);
        if (event == null) {
            throw new RuntimeException("事件不存在");
        }
        return event;
    }

    @Override
    public void add(Event event) {
        if (event.getWisdomBonus() == null) event.setWisdomBonus(0);
        if (event.getForceBonus() == null) event.setForceBonus(0);
        if (event.getSocialBonus() == null) event.setSocialBonus(0);
        if (event.getAgilityBonus() == null) event.setAgilityBonus(0);
        eventMapper.insert(event);
    }

    @Override
    public void update(Event event) {
        Event exist = eventMapper.selectById(event.getId());
        if (exist == null) {
            throw new RuntimeException("事件不存在");
        }
        eventMapper.update(event);
    }

    @Override
    public void delete(Long id) {
        eventMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeEvent(Long characterId, Long eventId) {
        Character character = characterMapper.selectById(characterId);
        if (character == null) {
            throw new RuntimeException("角色不存在");
        }
        Event event = eventMapper.selectById(eventId);
        if (event == null) {
            throw new RuntimeException("事件不存在");
        }

        character.setWisdom(character.getWisdom() + event.getWisdomBonus());
        character.setForce(character.getForce() + event.getForceBonus());
        character.setSocial(character.getSocial() + event.getSocialBonus());
        character.setAgility(character.getAgility() + event.getAgilityBonus());
        characterMapper.update(character);

        CharacterEvent characterEvent = new CharacterEvent();
        characterEvent.setCharacterId(characterId);
        characterEvent.setEventId(eventId);
        characterEventMapper.insert(characterEvent);
    }
}