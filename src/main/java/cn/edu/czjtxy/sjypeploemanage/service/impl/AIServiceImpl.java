package cn.edu.czjtxy.sjypeploemanage.service.impl;

import cn.edu.czjtxy.sjypeploemanage.entity.Character;
import cn.edu.czjtxy.sjypeploemanage.entity.CharacterEvent;
import cn.edu.czjtxy.sjypeploemanage.entity.Event;
import cn.edu.czjtxy.sjypeploemanage.mapper.CharacterEventMapper;
import cn.edu.czjtxy.sjypeploemanage.mapper.CharacterMapper;
import cn.edu.czjtxy.sjypeploemanage.mapper.EventMapper;
import cn.edu.czjtxy.sjypeploemanage.service.AIService;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AIServiceImpl implements AIService {

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.model}")
    private String model;

    @Autowired
    private CharacterMapper characterMapper;

    @Autowired
    private CharacterEventMapper characterEventMapper;

    @Autowired
    private EventMapper eventMapper;

    @Override
    public Map<String, Object> getCharacterTrainingSummary(Long characterId) {
        Character character = characterMapper.selectById(characterId);
        if (character == null) {
            throw new RuntimeException("角色不存在");
        }

        List<CharacterEvent> characterEvents = characterEventMapper.selectByCharacterId(characterId);
        int completedEventCount = characterEvents != null ? characterEvents.size() : 0;

        StringBuilder eventHistory = new StringBuilder();
        if (characterEvents != null) {
            for (CharacterEvent ce : characterEvents) {
                Event event = eventMapper.selectById(ce.getEventId());
                if (event != null) {
                    eventHistory.append("- ").append(event.getName())
                            .append(" (智慧+").append(event.getWisdomBonus())
                            .append(",武力+").append(event.getForceBonus())
                            .append(",社交+").append(event.getSocialBonus())
                            .append(",敏捷+").append(event.getAgilityBonus())
                            .append(")\n");
                }
            }
        }

        int total = character.getWisdom() + character.getForce() + character.getSocial() + character.getAgility();
        int avg = total / 4;

        Map<String, Integer> attrMap = new LinkedHashMap<>();
        attrMap.put("智慧", character.getWisdom());
        attrMap.put("武力", character.getForce());
        attrMap.put("社交", character.getSocial());
        attrMap.put("敏捷", character.getAgility());

        String strongestKey = attrMap.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("智慧");
        String weakestKey = attrMap.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("敏捷");
        int balance = attrMap.get(strongestKey) - attrMap.get(weakestKey);

        String stage;
        if (total < 40) stage = "初始培养";
        else if (total < 80) stage = "成长期";
        else if (total < 120) stage = "成熟期";
        else stage = "精英期";

        String prompt = String.format(
                "请作为一个人物培养分析专家，对以下角色进行全面的培养总结并给出后续培养建议。\n\n" +
                        "角色名称：%s\n" +
                        "性别：%s\n" +
                        "当前属性值：智慧 %d, 武力 %d, 社交 %d, 敏捷 %d\n" +
                        "已完成的事件历史：\n%s\n\n" +
                        "请严格按照以下JSON格式返回（不要有任何其他文字）：\n" +
                        "{\n" +
                        "  \"summary\": \"对该角色当前培养状态的整体总结（2-3段文字）\",\n" +
                        "  \"suggestions\": [\n" +
                        "    {\"title\": \"建议标题\", \"content\": \"具体建议内容\", \"priority\": \"high|medium|low\"}\n" +
                        "  ]\n" +
                        "}\n" +
                        "至少给出3条建议。",
                character.getName(),
                character.getGender(),
                character.getWisdom(),
                character.getForce(),
                character.getSocial(),
                character.getAgility(),
                eventHistory.length() > 0 ? eventHistory.toString() : "暂无"
        );

        String aiResponse = callAIAPI(prompt);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("characterName", character.getName());
        result.put("stage", stage);
        result.put("total", total);
        result.put("avg", avg);
        result.put("balance", balance);
        result.put("completedEventCount", completedEventCount);
        result.put("recordCount", completedEventCount);
        result.put("generateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Map<String, Object> strongest = new LinkedHashMap<>();
        strongest.put("label", strongestKey);
        strongest.put("value", attrMap.get(strongestKey));
        result.put("strongest", strongest);

        Map<String, Object> weakest = new LinkedHashMap<>();
        weakest.put("label", weakestKey);
        weakest.put("value", attrMap.get(weakestKey));
        result.put("weakest", weakest);

        try {
            JSONObject json = JSON.parseObject(aiResponse);
            result.put("summary", json.getString("summary"));
            JSONArray sugs = json.getJSONArray("suggestions");
            if (sugs != null) {
                result.put("suggestions", sugs);
            } else {
                result.put("suggestions", new ArrayList<>());
            }
        } catch (Exception e) {
            result.put("summary", aiResponse);
            result.put("suggestions", new ArrayList<>());
        }

        return result;
    }

    @Override
    public Map<String, Object> getGlobalTrainingSummary(Long userId) {
        List<Character> characters = characterMapper.selectList(userId);
        if (characters == null || characters.isEmpty()) {
            Map<String, Object> empty = new LinkedHashMap<>();
            empty.put("summary", "暂无角色数据，无法生成全局培养态势分析。");
            empty.put("suggestions", new ArrayList<>());
            empty.put("generateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return empty;
        }

        StringBuilder charInfo = new StringBuilder();
        int totalEvents = 0;
        for (Character c : characters) {
            List<CharacterEvent> ces = characterEventMapper.selectByCharacterId(c.getId());
            int eventCount = ces != null ? ces.size() : 0;
            totalEvents += eventCount;
            charInfo.append("- ").append(c.getName())
                    .append("（").append(c.getGender()).append("）")
                    .append("：智慧").append(c.getWisdom())
                    .append("，武力").append(c.getForce())
                    .append("，社交").append(c.getSocial())
                    .append("，敏捷").append(c.getAgility())
                    .append("，已完成事件").append(eventCount).append("个\n");
        }

        String prompt = String.format(
                "请作为一个人物培养分析专家，对以下用户的所有角色进行全局培养态势分析。\n\n" +
                        "角色总数：%d\n" +
                        "各角色详情：\n%s\n" +
                        "已完成事件总数：%d\n\n" +
                        "请严格按照以下JSON格式返回（不要有任何其他文字）：\n" +
                        "{\n" +
                        "  \"summary\": \"全局培养态势分析（2-3段文字，包含整体概况、对比分析、策略建议）\",\n" +
                        "  \"suggestions\": [\n" +
                        "    {\"title\": \"建议标题\", \"content\": \"具体建议内容\", \"priority\": \"high|medium|low\"}\n" +
                        "  ]\n" +
                        "}\n" +
                        "至少给出3条建议。",
                characters.size(),
                charInfo.toString(),
                totalEvents
        );

        String aiResponse = callAIAPI(prompt);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("generateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        try {
            JSONObject json = JSON.parseObject(aiResponse);
            result.put("summary", json.getString("summary"));
            JSONArray sugs = json.getJSONArray("suggestions");
            if (sugs != null) {
                result.put("suggestions", sugs);
            } else {
                result.put("suggestions", new ArrayList<>());
            }
        } catch (Exception e) {
            result.put("summary", aiResponse);
            result.put("suggestions", new ArrayList<>());
        }

        return result;
    }

    private String callAIAPI(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        JSONArray messages = new JSONArray();
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一个专业的人物培养分析专家，擅长分析角色属性并给出培养建议。必须严格按照用户要求的JSON格式返回结果，不要输出任何JSON之外的内容。");
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);

        try {
            HttpResponse response = HttpRequest.post(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(requestBody))
                    .timeout(60000)
                    .execute();

            if (response.isOk()) {
                JSONObject jsonResponse = JSON.parseObject(response.body());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    return message.getString("content");
                }
            }
            return "{\"summary\":\"AI接口调用失败: " + response.getStatus() + "\",\"suggestions\":[]}";
        } catch (Exception e) {
            return "{\"summary\":\"AI接口调用异常: " + e.getMessage() + "\",\"suggestions\":[]}";
        }
    }
}