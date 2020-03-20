package com.yusheng.hbgj.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

import com.yusheng.hbgj.entity.Publish;

@Mapper
public interface PublishDao {

    @Select("select * from publish t where t.id = #{id}")
    Publish getById(Long id);

    @Delete("delete from publish where id = #{id}")
    int delete(Long id);


    
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into publish(openid, sex, tel, nickName, headPic, publishContent, createTime, updateTime, createName, updateName, remark, orgId, delFlag, " +
            "sort, delTime,reply) " +
            "values(#{openid}, #{sex}, #{tel}, #{nickName}, #{headPic}, #{publishContent}, #{createTime}, #{updateTime}, #{createName}, #{updateName}, #{remark}," +
            " #{orgId}, #{delFlag}, #{sort}, #{delTime},#{reply})")
    int save(Publish publish);

    @Update("update publish t set t.delFlag='0', t.delTime=now() where id=#{id} ")
    int logDel(String id);

    int update(Publish publish);

    int count(@Param("params") Map<String, Object> params);

    List<Publish> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
