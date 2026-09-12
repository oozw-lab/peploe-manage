package cn.edu.czjtxy.sjypeploemanage.controller;

import cn.edu.czjtxy.sjypeploemanage.common.Result;
import cn.edu.czjtxy.sjypeploemanage.entity.Character;
import cn.edu.czjtxy.sjypeploemanage.entity.Event;
import cn.edu.czjtxy.sjypeploemanage.mapper.CharacterEventMapper;
import cn.edu.czjtxy.sjypeploemanage.service.CharacterService;
import cn.edu.czjtxy.sjypeploemanage.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
public class StatisticsController {

    @Autowired
    private CharacterService characterService;

    @Autowired
    private EventService eventService;

    @Autowired
    private CharacterEventMapper characterEventMapper;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview(@RequestParam Long userId) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<Character> characters = characterService.list(userId);
        List<Event> events = eventService.list(userId);

        result.put("characterCount", characters.size());
        result.put("eventCount", events.size());

        int completed = 0, assigned = 0, draft = 0, failed = 0;
        for (Event e : events) {
            int st = e.getStatus() != null ? e.getStatus() : 0;
            switch (st) {
                case 0: draft++; break;
                case 1: assigned++; break;
                case 2: completed++; break;
                case 3: failed++; break;
            }
        }
        result.put("completedEventCount", completed);
        result.put("assignedEventCount", assigned);
        result.put("draftEventCount", draft);
        result.put("failedEventCount", failed);
        result.put("completionRate", events.isEmpty() ? 0 : Math.round((float) completed / events.size() * 100));

        Map<String, Object> attrAvg = new LinkedHashMap<>();
        if (!characters.isEmpty()) {
            int wisdom = 0, force = 0, social = 0, agility = 0;
            for (Character c : characters) {
                wisdom += c.getWisdom() != null ? c.getWisdom() : 0;
                force += c.getForce() != null ? c.getForce() : 0;
                social += c.getSocial() != null ? c.getSocial() : 0;
                agility += c.getAgility() != null ? c.getAgility() : 0;
            }
            int n = characters.size();
            attrAvg.put("wisdom", Math.round((float) wisdom / n));
            attrAvg.put("force", Math.round((float) force / n));
            attrAvg.put("social", Math.round((float) social / n));
            attrAvg.put("agility", Math.round((float) agility / n));
        }
        result.put("attributeAvg", attrAvg);

        List<Map<String, Object>> records = characterEventMapper.selectAllWithDetails(null);
        result.put("recordCount", records.size());

        return Result.success(result);
    }
}
