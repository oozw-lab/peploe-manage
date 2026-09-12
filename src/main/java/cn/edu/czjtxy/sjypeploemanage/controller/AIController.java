package cn.edu.czjtxy.sjypeploemanage.controller;

import cn.edu.czjtxy.sjypeploemanage.common.Result;
import cn.edu.czjtxy.sjypeploemanage.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AIController {

    @Autowired
    private AIService aiService;

    @GetMapping("/summary/{characterId}")
    public Result<Map<String, Object>> getSummary(@PathVariable Long characterId) {
        Map<String, Object> data = aiService.getCharacterTrainingSummary(characterId);
        return Result.success("获取成功", data);
    }

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview(@RequestParam(required = false) Long userId) {
        Map<String, Object> data = aiService.getGlobalTrainingSummary(userId);
        return Result.success("获取成功", data);
    }
}
