package com.yusheng.hbgj.dao;

import com.yusheng.hbgj.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserDao {


    @Update("update sys_user t set  t.openid = #{openid} , t.headImgUrl= #{headImgUrl}   where t.id = #{id} ")
    Integer saveOpenid(@Param("id") Long id, @Param("openid") String openid, String headImgUrl);

    @Update("update sys_user t set  t.openid = #{openid}    where t.id = #{id} ")
    Integer saveOpenidHb(@Param("id") Long id, @Param("openid") String openid);


    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into sys_user(username, password, salt, nickname, headImgUrl, phone, " +
            "telephone, email, birthday, sex, status, createTime, updateTime,originalPassword,remark,address,openid,compFlag,agreeLicence , contractBeginDate,contractEndDate ) values " +
            "(#{username}, #{password}, #{salt}, #{nickname}, #{headImgUrl}, #{phone}, #{telephone}, " +
            "#{email}, #{birthday}, #{sex}, #{status}, now(), now(),#{originalPassword},#{remark},#{address},#{openid} ,#{compFlag},#{agreeLicence}, #{contractBeginDate}, #{contractEndDate}  )")
    int save(User user);


    @Select(" select  id, headImgUrl  from sys_user where openid=#{openid}  and remark= #{remark}  ")
    User getVisitor(String openid, String remark);


    @Delete(" delete  from sys_user where  id=#{id}  ")
    int delete(Long id);


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

    @Select("select  t.id  from sys_user t where t.openid= #{openid} ")
    Long getUserIdAA(String openid);

    @Select("select t.* from sys_user t where t.openid = #{openid} and t.compFlag=#{compFlag}")
    User getInfoByOpenId(String openid, Integer compFlag);

    @Update("update sys_user t set  t.agreeLicence= now()  where t.id = #{id} and t.compFlag=1 and t.agreeLicence is  null ")
    int agreeLicence(String userId);

    @Update("update sys_user t set t.status= 2  where  t.username!=null AND  t.username = #{username} ")
    void lockAccount(String username);

    @Select(" SELECT  MD5( concat(t.username,t.originalPassword)) from sys_user t where t.openid=#{openid} ")
    String getTokenByOpenId(String openid);

}
