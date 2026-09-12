package cn.edu.czjtxy.sjypeploemanage.mapper;

import cn.edu.czjtxy.sjypeploemanage.entity.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EventMapper {

    List<Event> selectList(@Param("userId") Long userId);

    Event selectById(@Param("id") Long id);

    int insert(Event event);

    int update(Event event);

    int deleteById(@Param("id") Long id);
}