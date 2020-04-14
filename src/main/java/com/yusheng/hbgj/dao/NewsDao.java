package com.yusheng.hbgj.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

import com.yusheng.hbgj.entity.News;

@Mapper
public interface NewsDao {

    @Select("select * from news t where t.id = #{id}")
    News getById(Long id);

    @Delete("delete from news where id = #{id}")
    int delete(Long id);

     @Update("update news t set t.delFlag='0',t.delTime=now() where id=#{id} ")
     int logDel(String id);

    
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into news(title, content, author, createTime, updateTime, remark, delFlag, sort,bannerImg) values(#{title}, #{content}, #{author}, #{createTime}, #{updateTime}, #{remark}, #{delFlag}, #{sort},#{bannerImg} )")
    int save(News news);

    int update(News news);

    int count(@Param("params") Map<String, Object> params);

    List<News> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
