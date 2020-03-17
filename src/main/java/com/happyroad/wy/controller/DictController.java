package com.happyroad.wy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.happyroad.wy.dao.DictDao;
import com.happyroad.wy.entity.Dict;
import com.happyroad.wy.page.table.PageTableHandler;
import com.happyroad.wy.page.table.PageTableRequest;
import com.happyroad.wy.page.table.PageTableResponse;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dicts")
public class DictController {

    @Autowired
    private DictDao dictDao;

    @RequiresPermissions("dict:add")
    @PostMapping
    @ApiOperation(value = "保存")
    public Dict save(@RequestBody Dict dict) {
        Dict d = dictDao.getByTypeAndK(dict.getType(), dict.getK());
        if (d != null) {
            throw new IllegalArgumentException("类型和key已存在");
        }
        dictDao.save(dict);

        return dict;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取")
    public Dict get(@PathVariable Long id) {
        return dictDao.getById(id);
    }

    @RequiresPermissions("dict:add")
    @PutMapping
    @ApiOperation(value = "修改")
    public Dict update(@RequestBody Dict dict) {
        dictDao.update(dict);

        return dict;
    }

    @RequiresPermissions("dict:query")
    @GetMapping(params = {"start", "length"})
    @ApiOperation(value = "列表")
    public PageTableResponse list(PageTableRequest request) {

        Map<String,Object> params=new HashMap<>();
        params.put("orderBy"," ORDER BY  t.createTime DESC");

        request.setParams(params);

        return new PageTableHandler(request12 -> dictDao.count(request12.getParams()), request1 -> dictDao.list(request1.getParams(), request1.getOffset(), request1.getLimit())).handle(request);
    }

    @RequiresPermissions("dict:del")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    public void delete(@PathVariable Long id) {
        dictDao.delete(id);
    }

    @GetMapping(params = "type")
    public List<Dict> listByType(String type) {
        return dictDao.listByType(type);
    }
}
