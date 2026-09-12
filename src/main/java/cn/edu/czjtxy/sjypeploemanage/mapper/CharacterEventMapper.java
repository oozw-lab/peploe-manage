package cn.edu.czjtxy.sjypeploemanage.mapper;

import cn.edu.czjtxy.sjypeploemanage.entity.CharacterEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CharacterEventMapper {

    List<CharacterEvent> selectByCharacterId(@Param("characterId") Long characterId);

    List<Map<String, Object>> selectAllWithDetails(@Param("characterId") Long characterId);

    int insert(CharacterEvent characterEvent);

    int deleteById(@Param("id") Long id);
}