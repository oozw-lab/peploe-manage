package cn.edu.czjtxy.sjypeploemanage.controller;

import cn.edu.czjtxy.sjypeploemanage.common.Result;
import cn.edu.czjtxy.sjypeploemanage.entity.Event;
import cn.edu.czjtxy.sjypeploemanage.entity.Character;
import cn.edu.czjtxy.sjypeploemanage.mapper.CharacterEventMapper;
import cn.edu.czjtxy.sjypeploemanage.service.EventService;
import cn.edu.czjtxy.sjypeploemanage.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/event")
@CrossOrigin
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private CharacterService characterService;

    @Autowired
    private CharacterEventMapper characterEventMapper;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@RequestParam Long userId) {
        List<Event> list = eventService.list(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Event e : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", e.getId());
            map.put("name", e.getName());
            map.put("description", e.getDescription());
            map.put("category", e.getCategory());
            map.put("status", e.getStatus());
            map.put("wisdomBonus", e.getWisdomBonus());
            map.put("forceBonus", e.getForceBonus());
            map.put("socialBonus", e.getSocialBonus());
            map.put("agilityBonus", e.getAgilityBonus());
            map.put("characterId", e.getCharacterId());
            map.put("userId", e.getUserId());
            map.put("createTime", e.getCreateTime());
            map.put("updateTime", e.getUpdateTime());
            if (e.getCharacterId() != null) {
                try {
                    Character c = characterService.getById(e.getCharacterId());
                    map.put("characterName", c.getName());
                } catch (Exception ex) {
                    map.put("characterName", null);
                }
            } else {
                map.put("characterName", null);
            }
            result.add(map);
        }
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Event> getById(@PathVariable Long id) {
        Event event = eventService.getById(id);
        return Result.success(event);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Event event) {
        if (event.getName() == null) {
            return Result.error("事件名称不能为空");
        }
        if (event.getCategory() == null) {
            event.setCategory(6);
        }
        if (event.getStatus() == null) {
            event.setStatus(0);
        }
        eventService.add(event);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Event event) {
        eventService.update(event);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return Result.success();
    }

    @PostMapping("/assign")
    public Result<Void> assignEvent(@RequestBody Map<String, Long> params) {
        Long eventId = params.get("eventId");
        Long characterId = params.get("characterId");
        if (eventId == null) {
            return Result.error("事件ID不能为空");
        }
        Event event = eventService.getById(eventId);
        if (event == null) {
            return Result.error("事件不存在");
        }
        event.setCharacterId(characterId);
        event.setStatus(characterId != null ? 1 : 0);
        eventService.update(event);
        return Result.success("指派成功", null);
    }

    @PostMapping("/complete")
    public Result<Void> completeEvent(@RequestBody Map<String, Object> params) {
        Long characterId = params.get("characterId") != null ? Long.valueOf(params.get("characterId").toString()) : null;
        Long eventId = params.get("eventId") != null ? Long.valueOf(params.get("eventId").toString()) : null;
        Boolean success = params.get("success") != null ? Boolean.valueOf(params.get("success").toString()) : true;
        if (characterId == null || eventId == null) {
            return Result.error("角色ID和事件ID不能为空");
        }
        eventService.completeEvent(characterId, eventId);
        Event event = eventService.getById(eventId);
        event.setStatus(success ? 2 : 3);
        eventService.update(event);
        return Result.success("事件完成成功，角色属性已增加", null);
    }

    @GetMapping("/records")
    public Result<List<Map<String, Object>>> records(
            @RequestParam(required = false) Long characterId,
            @RequestParam(required = false) Long userId) {
        List<Map<String, Object>> list = characterEventMapper.selectAllWithDetails(characterId);
        return Result.success(list);
    }
}