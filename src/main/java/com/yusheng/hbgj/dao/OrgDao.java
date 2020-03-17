package com.yusheng.hbgj.dao;

import com.yusheng.hbgj.entity.Org;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrgDao {

	@Select("select * from sys_orgTree t order by t.sort")
	List<Org> listAll();

	@Select("select * from sys_orgTree t where t.type = 1 order by t.sort")
	List<Org> listParents();

	@Select("select distinct p.* from sys_orgTree p inner join sys_role_permission rp on p.id = rp.permissionId inner join sys_role_user ru on ru.roleId = rp.roleId where ru.userId = #{userId} order by p.sort")
	List<Org> listByUserId(Long userId);

	@Select("select p.* from sys_orgTree p inner join sys_role_permission rp on p.id = rp.permissionId where rp.roleId = #{roleId} order by p.sort")
	List<Org> listByRoleId(Long roleId);

	@Select("select * from sys_orgTree t where t.id = #{id}")
	Org getById(Long id);

	@Insert("insert into sys_orgTree(parentId, name, css, href, type, permission, sort) values(#{parentId}, #{name}, #{css}, #{href}, #{type}, #{permission}, #{sort})")
	int save(Org permission);

	@Update("update sys_orgTree t set parentId = #{parentId}, name = #{name}, css = #{css}, href = #{href}, type = #{type}, permission = #{permission}, sort = #{sort} where t.id = #{id}")
	int update(Org permission);

	@Delete("delete from sys_orgTree where id = #{id}")
	int delete(Long id);
	
	@Delete("delete from sys_orgTree where parentId = #{id}")
	int deleteByParentId(Long id);


}
