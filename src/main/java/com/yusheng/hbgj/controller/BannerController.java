package com.yusheng.hbgj.controller;

import com.yusheng.hbgj.annotation.PermissionTag;
import com.yusheng.hbgj.dao.BannerDao;
import com.yusheng.hbgj.entity.Banner;
import com.yusheng.hbgj.entity.News;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banners")
public class BannerController {

    @Autowired
    private BannerDao bannerDao;

    @PostMapping
    @ApiOperation(value = "保存")
    public Banner save(@RequestBody Banner banner) {
        bannerDao.save(banner);

        return banner;
    }


    @PutMapping("/toTop")
    @ApiOperation(value = "置顶展示")
    @PermissionTag("sys:banner:top")
    public Banner toTop(@RequestBody Banner banner) {

        banner.setSort(System.currentTimeMillis());
        bannerDao.update(banner);
        return banner;
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取")
    public Banner get(@PathVariable Long id) {
        return bannerDao.getById(id);
    }

    @PutMapping
    @ApiOperation(value = "修改")
    public Banner update(@RequestBody Banner banner) {
        bannerDao.update(banner);

        return banner;
    }

    @GetMapping
    @ApiOperation(value = "列表")
    public PageTableResponse list(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return bannerDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<Banner> list(PageTableRequest request) {

                request.getParams().putIfAbsent("orderBy", "  ORDER BY createTime DESC  ");

                return bannerDao.list(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    public void delete(@PathVariable Long id) {
        bannerDao.delete(id);
    }
}
