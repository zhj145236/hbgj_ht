package com.yusheng.hbgj.dao;

import java.util.List;
import java.util.Map;

import com.yusheng.hbgj.dto.FileDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yusheng.hbgj.entity.FileInfo;

@Mapper
public interface FileInfoDao {

    @Select("select * from file_info t where t.id = #{id} order by createTime DESC ")
    FileInfo getById(String id);

    @Insert("insert into file_info(id, contentType, size, path, url, type, createTime, resourceId,tag,uploadTime ,fileOriginName ,sort,delTime,remark,orgId,md5 ) values " +
            "(#{id}, #{contentType}, #{size}, #{path}, #{url}, #{type}, now(), #{resourceId},#{tag},#{uploadTime} ,#{fileOriginName},#{sort},#{delTime}, #{remark},#{orgId},#{md5})")
    int save(FileInfo fileInfo);


    @Update("update file_info t set t.updateTime = now() where t.id = #{id}")
    int update(FileInfo fileInfo);

    @Update("update file_info t set t.updateTime = now() ,  t.remark=#{remark} where t.id = #{id}")
    int saveRemark(FileInfo fileInfo);


    @Delete("delete from file_info where id = #{id}")
    int delete(String id);


    @Update("update file_info t set t.delFlag=0, t.delTime=now() where id=#{id} ")
    int logDel(String id);


    int count(@Param("params") Map<String, Object> params);

    @Select("select count(1) from file_info t where t.md5 = #{md5}  ")
    int fileExtist(@Param("md5") String md5);



    @Select("select count(1) from file_info t  where t.resourceId = #{resourceId}  and  t.delFlag='1'  ")
    int fileRefCount(@Param("resourceId") String resourceId);


    List<FileInfo> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                        @Param("limit") Integer limit);


    @Select("select  fileOriginName  from file_info t where t.url = #{url}  limit 0, 1  ")
    String getByUrl(String url);


    List<FileDto> wxlistFiles(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                              @Param("limit") Integer limit);

    int wxCount(@Param("params") Map<String, Object> params);

}
