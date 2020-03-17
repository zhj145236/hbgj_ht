package com.yusheng.hbgj.dao;

import com.yusheng.hbgj.entity.TableAnaly;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020/1/21 16:55
 * @desc 表数据库统计
 */
@Mapper
public interface TableAnalyDao {


    List<Integer>  thisTableCurrentRows( String tName );

    List<TableAnaly> getCurrenData();

    Integer insertBatch(@Param(value = "list") List<TableAnaly> list);


}
