package com.happyroad.wy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.happyroad.wy.annotation.LogAnnotation;
import com.happyroad.wy.entity.Org;
import com.happyroad.wy.service.OrgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 机构接口
 *
 * @author Jinwei
 *
 */
@Api(tags = "权限")
@RestController
@RequestMapping("/org")
public class OrgController {


	@Autowired
	private OrgService orgService;


	/**
	 * 设置子元素
	 * 2018.06.09
	 *
	 * @param p
	 * @param
	 */
	private void setChild(Org p, List<Org> orgs) {
		List<Org> child = orgs.parallelStream().filter(a -> a.getParentId().equals(p.getId())).collect(Collectors.toList());
		p.setChild(child);
		if (!CollectionUtils.isEmpty(child)) {
			child.parallelStream().forEach(c -> {
				//递归设置子元素，多级机构支持
				setChild(c, orgs);
			});
		}
	}


	/**
	 * 机构列表
	 *
	 * @param pId
	 * @param orgsAll
	 * @param list
	 */
	private void setOrgsList(Long pId, List<Org> orgsAll, List<Org> list) {
		for (Org per : orgsAll) {
			if (per.getParentId().equals(pId)) {
				list.add(per);
				if (orgsAll.stream().filter(p -> p.getParentId().equals(per.getId())).findAny() != null) {
					setOrgsList(per.getId(), orgsAll, list);
				}
			}
		}
	}

	@GetMapping
	@ApiOperation(value = "机构列表")
	//@RequiresPermissions("sys:org:query")
	public List<Org> orgsList() {
		List<Org> orgsAll = orgService.listAll();

		List<Org> list = Lists.newArrayList();
		setOrgsList(0L, orgsAll, list);

		return list;
	}

	@GetMapping("/all")
	@ApiOperation(value = "所有机构")
	//@RequiresPermissions("sys:org:query")
	public JSONArray orgsAll() {
	    
		List<Org> orgsAll = orgService.listAll();
		JSONArray array = new JSONArray();
		setOrgsTree(0L, orgsAll, array);

		return array;
	}

	@GetMapping("/parents")
	@ApiOperation(value = "一级机构")
	@RequiresPermissions("sys:org:query")
	public List<Org> parentOrg() {
		List<Org> parents = orgService.listParents();

		return parents;
	}

	/**
	 * 机构树
	 *
	 * @param
	 * @param
	 * @param
	 */
	private void setOrgsTree(Long pId, List<Org> orgsAll, JSONArray array) {
		for (Org per : orgsAll) {
			if (per.getParentId().equals(pId)) {
				String string = JSONObject.toJSONString(per);
				JSONObject parent = (JSONObject) JSONObject.parse(string);
				array.add(parent);

				if (orgsAll.stream().filter(p -> p.getParentId().equals(per.getId())).findAny() != null) {
					JSONArray child = new JSONArray();
					parent.put("child", child);
					setOrgsTree(per.getId(), orgsAll, child);
				}
			}
		}
	}

	 

	@LogAnnotation
	@PostMapping
	@ApiOperation(value = "保存机构")
	@RequiresPermissions("sys:org:add")
	public void save(@RequestBody Org org) {
		orgService.save(org);
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "根据机构id获取机构")
	@RequiresPermissions("sys:org:query")
	public Org get(@PathVariable Long id) {
		return orgService.getById(id);
	}

	@LogAnnotation
	@PutMapping
	@ApiOperation(value = "修改机构")
	@RequiresPermissions("sys:org:add")
	public void update(@RequestBody Org org) {
		orgService.update(org);
	}

	/**
	 * 校验权限
	 *
	 * @return
	 */
	/*@GetMapping("/owns")
	@ApiOperation(value = "校验当前用户的权限")
	public Set<String> ownsOrg() {
		List<Org> orgs = UserUtil.getCurrentOrgs();
		if (CollectionUtils.isEmpty(orgs)) {
			return Collections.emptySet();
		}

		return orgs.parallelStream().filter(p -> !StringUtils.isEmpty(p.getOrg()))
				.map(Org::get).collect(Collectors.toSet());
	}*/

	@LogAnnotation
	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除机构")
	@RequiresPermissions(value = { "sys:org:del" })
	public void delete(@PathVariable Long id) {
		orgService.delete(id);
	}
}
