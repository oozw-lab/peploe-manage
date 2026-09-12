package cn.edu.czjtxy.sjypeploemanage.service;

import java.util.Map;

public interface AIService {

    Map<String, Object> getCharacterTrainingSummary(Long characterId);

    Map<String, Object> getGlobalTrainingSummary(Long userId);
}