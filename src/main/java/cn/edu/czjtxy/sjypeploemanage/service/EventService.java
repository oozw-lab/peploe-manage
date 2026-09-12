package cn.edu.czjtxy.sjypeploemanage.service;

import cn.edu.czjtxy.sjypeploemanage.entity.Event;

import java.util.List;

public interface EventService {

    List<Event> list(Long userId);

    Event getById(Long id);

    void add(Event event);

    void update(Event event);

    void delete(Long id);

    void completeEvent(Long characterId, Long eventId);
}