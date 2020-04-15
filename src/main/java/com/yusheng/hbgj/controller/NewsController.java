package com.yusheng.hbgj.controller;

import java.util.Date;
import java.util.List;

import com.yusheng.hbgj.annotation.PermissionTag;
import com.yusheng.hbgj.constants.BusinessException;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yusheng.hbgj.dao.NewsDao;
import com.yusheng.hbgj.entity.News;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/newss")
public class NewsController {

    @Autowired
    private NewsDao newsDao;

    @PostMapping
    @ApiOperation(value = "保存")
    @PermissionTag("sys:news:save")
    public News save(@RequestBody News news) {

        news.setDelFlag("1");
        news.setCreateTime(new Date());
        news.setUpdateTime(new Date());

        newsDao.save(news);

        return news;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取")
    public News get(@PathVariable Long id) {
        return newsDao.getById(id);
    }

    @PutMapping
    @ApiOperation(value = "修改")
    @PermissionTag("sys:news:save")
    public News update(@RequestBody News news) {

        news.setUpdateTime(new Date());


        newsDao.update(news);

        return news;
    }

    @PutMapping("/toTop")
    @ApiOperation(value = "置顶展示")
    @PermissionTag("sys:news:top")
    public News toTop(@RequestBody News news) {

        news.setSort(System.currentTimeMillis());
        newsDao.update(news);
        return news;
    }


    @GetMapping
    @ApiOperation(value = "列表")
    public PageTableResponse list(PageTableRequest request) {


        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return newsDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<News> list(PageTableRequest request) {

                request.getParams().putIfAbsent("orderBy", "  ORDER BY  sort DESC, createTime DESC   ");

                return newsDao.list(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    @PermissionTag("sys:news:delete")
    public void delete(@PathVariable Long id) {
        newsDao.delete(id);
    }
}
