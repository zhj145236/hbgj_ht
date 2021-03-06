package com.yusheng.hbgj.dao;

import java.util.List;
import java.util.Map;

import com.yusheng.hbgj.dto.NoticeDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yusheng.hbgj.dto.NoticeReadVO;
import com.yusheng.hbgj.entity.Notice;
import com.yusheng.hbgj.entity.User;

@Mapper
public interface NoticeDao {

    @Select("select * from t_notice t where t.id = #{id}")
    Notice getById(Long id);

    @Delete("delete from t_notice where id = #{id}")
    int delete(Long id);


    /***
     * 删除特定的数据
     * @return
     */
    @Delete("delete from t_notice where   refId=#{refId}  and createTime>= now() ")
    int flushNotice(@Param("refId") String refId);


    @Delete("delete from t_notice where   refId=#{refId}  and  receiveId = #{receiveId}  and title= #{title} and createTime>= now() ")
    int dropNotice(@Param("refId") String refId, String receiveId, String title);

    @Update("update t_notice t set title = #{title}, content = #{content}, status = #{status}, updateTime = #{updateTime} where t.id = #{id}")
    int update(Notice notice);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into t_notice(title, content, status, createTime, updateTime,receiveId, isPersonal, refId) values(#{title}, #{content}, #{status}, #{createTime}, #{updateTime},#{receiveId},#{isPersonal}, #{refId} )")
    int save(Notice notice);

    int count(@Param("params") Map<String, Object> params);

    List<Notice> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                      @Param("limit") Integer limit);

    @Insert("insert ignore into t_notice_read(noticeId, userId, createTime) values(#{noticeId}, #{userId}, now())")
    int saveReadRecord(@Param("noticeId") Long noticeId, @Param("userId") Long userId);

    List<User> listReadUsers(Long noticeId);

    @Select(" select  count(1) from   ( SELECT id from  t_notice  WHERE   status = 1  AND  ( isPersonal= '0'  OR  ( isPersonal='1' AND  receiveId= #{userId} ) ) )  q  where q.id not in ( SELECT noticeId from t_notice_read where userId= #{userId} )  ")
    int countUnread(Long userId);

    int countNotice(@Param("params") Map<String, Object> params);

    List<NoticeReadVO> listNotice(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);


    /**
     * 将草稿变成发布状态
     */
    @Update(" UPDATE t_notice set `status`=1 where    isPersonal='1'  AND    ( createTime  BETWEEN date_add(now(), interval -3 day) and  now() ) ")
    public void autoPublish();


    List<NoticeDto> wxlistNotice(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                                 @Param("limit") Integer limit);

}
