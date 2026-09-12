package cn.edu.czjtxy.sjypeploemanage.mapper;

import cn.edu.czjtxy.sjypeploemanage.entity.Character;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CharacterMapper {

    List<Character> selectList(@Param("userId") Long userId);

    Character selectById(@Param("id") Long id);

    int insert(Character character);

    int update(Character character);

    int adjust(@Param("id") Long id,
               @Param("wisdom") Integer wisdom,
               @Param("force") Integer force,
               @Param("social") Integer social,
               @Param("agility") Integer agility);

    int deleteById(@Param("id") Long id);
}