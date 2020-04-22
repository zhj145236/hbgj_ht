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
            "telephone, email, birthday, sex, status, createTime, updateTime,originalPassword,remark,address,openid,compFlag,agreeLicence) values " +
            "(#{username}, #{password}, #{salt}, #{nickname}, #{headImgUrl}, #{phone}, #{telephone}, " +
            "#{email}, #{birthday}, #{sex}, #{status}, now(), now(),#{originalPassword},#{remark},#{address},#{openid} ,#{compFlag},#{agreeLicence}  )")
    int save(User user);

    @Select("select * from sys_user t where t.id = #{id}")
    User getById(Long id);

    @Select("select t.* from sys_user t where t.username = #{username}")
    User getUser(String username);

    @Update("update sys_user t set t.password = #{password},originalPassword=#{originalPassword} where t.id = #{id}")
    int changePassword(@Param("id") Long id, @Param("password") String password, @Param("originalPassword") String originalPassword);


    @Select("select t.id,t.nickname from sys_user t where  t.compFlag = 1 and  t.id not in (1,2) ")
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

    @Select("select  t.id  from sys_user t where t.openid= #{openid}")
    Long getUserId(String openid);

    @Select("select t.* from sys_user t where t.openid = #{openid}")
    User getInfoByOpenId(String openid);

    @Update("update sys_user t set  t.agreeLicence= now()  where t.id = #{id} and t.compFlag=1 and t.agreeLicence is  null ")
    int agreeLicence(String userId);

    @Update("update sys_user t set t.status= 2  where  t.username!=null AND  t.username = #{username} ")
    void lockAccount(String username);

    @Select(" SELECT  MD5( concat(t.username,t.originalPassword)) from sys_user t where t.openid=#{openid} ")
    String getTokenByOpenId(String openid);

}
