package com.yusheng.hbgj.dao;

import java.util.List;
import java.util.Map;

import com.yusheng.hbgj.dto.PublishDto;
import org.apache.ibatis.annotations.*;

import com.yusheng.hbgj.entity.Publish;

@Mapper
public interface PublishDao {

    @Select("select * from publish t where t.id = #{id}")
    Publish getById(Long id);

    @Delete("delete from publish where id = #{id}")
    int delete(Long id);


    int save(Publish publish);

    @Update("update publish t set t.delFlag='0', t.delTime= now() where id=#{id} ")
    int logDel(String id);

    int update(Publish publish);

    int count(@Param("params") Map<String, Object> params);

    List<Publish> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);

    List<PublishDto> wxlist(Map<String, Object> params, Integer offset, Integer limit);

    @Select(" SELECT count(t.id) from publish t where  t.userId=13  AND t.userReadTime is null AND length(t.reply)>0  ")
    int getReplyButUnreadCount(Long userId);

}
