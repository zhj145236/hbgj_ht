package com.yusheng.hbgj.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

import com.yusheng.hbgj.entity.Banner;

@Mapper
public interface BannerDao {

    @Select("select * from banner t where t.id = #{id}")
    Banner getById(Long id);

    @Delete("delete from banner where id = #{id}")
    int delete(Long id);

    @Update("update banner t set t.delFlag='0', t.delTime=now() where id=#{id} ")
    int logDel(String id);


    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into banner(mainImg, content, author, createTime, updateTime, remark, delFlag, sort) values(#{mainImg}, #{content}, #{author}, #{createTime}, #{updateTime}, #{remark}, #{delFlag}, #{sort})")
    int save(Banner banner);

    int update(Banner banner);

    int count(@Param("params") Map<String, Object> params);

    List<Banner> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
