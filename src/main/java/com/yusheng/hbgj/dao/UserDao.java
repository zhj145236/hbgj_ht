package com.yusheng.hbgj.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yusheng.hbgj.entity.User;

@Mapper
public interface UserDao {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into sys_user(username, password, salt, nickname, headImgUrl, phone, " +
            "telephone, email, birthday, sex, status, createTime, updateTime,originalPassword,remark,address,openid) values " +
            "(#{username}, #{password}, #{salt}, #{nickname}, #{headImgUrl}, #{phone}, #{telephone}, " +
            "#{email}, #{birthday}, #{sex}, #{status}, now(), now(),#{originalPassword},#{remark},#{address},#{openid})")
    int save(User user);

    @Select("select * from sys_user t where t.id = #{id}")
    User getById(Long id);

    @Select("select * from sys_user t where t.username = #{username}")
    User getUser(String username);

    @Update("update sys_user t set t.password = #{password},originalPassword=#{originalPassword} where t.id = #{id}")
    int changePassword(@Param("id") Long id, @Param("password") String password, @Param("originalPassword") String originalPassword);


    @Select("select t.id,t.nickname from sys_user t where t.id not in (1,2) ")
    List<User> getAllUser();

    @Delete("delete from sys_role_user where userId = #{userId}")
    int deleteUserRole(Long userId);


    Integer count(@Param("params") Map<String, Object> params);

    List<User> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                    @Param("limit") Integer limit);


    int saveUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    int update(User user);

    @Select("select  count(*)  from sys_user t where t.openid= #{openid}")
    int wxCountByOpenid(String openid);

    @Select("select t.id  from sys_user t where t.openid= #{openid}")
    Long getUserId(String openid);

    @Select("select * from sys_user t where t.openid = #{openid}")
    User getInfoByOpenId(String openid);

}
