package com.yusheng.hbgj.controller;

import java.util.Date;
import java.util.List;

import com.yusheng.hbgj.dao.NoticeDao;
import com.yusheng.hbgj.dao.PublishDao;
import com.yusheng.hbgj.entity.Notice;
import com.yusheng.hbgj.entity.Publish;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import com.yusheng.hbgj.utils.DateUtil;
import com.yusheng.hbgj.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/publishs")
public class PublishController {

    @Autowired
    private PublishDao publishDao;


    @Autowired
    private NoticeDao noticeDao;

    @Value("${constants.adminId}")
    private String adminId;


    @PostMapping
    @ApiOperation(value = "保存")
    public Publish save(@RequestBody Publish publish) {

        publishDao.save(publish);

        // 给管理员单独 发送通知
        Notice notice = new Notice();
        notice.setContent("有用户于" + DateUtil.getNowStr() + "给您留言，请在【用户留言】中查看详情");
        notice.setTitle("用户留言");
        notice.setStatus(1);
        notice.setCreateTime(new Date());
        notice.setUpdateTime(new Date());
        notice.setIsPersonal(Notice.Personal.YES);
        notice.setReceiveId(adminId);


        noticeDao.save(notice);


        return publish;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取")
    public Publish get(@PathVariable Long id) {
        return publishDao.getById(id);
    }

    @PutMapping
    @ApiOperation(value = "修改")
    public Publish update(@RequestBody Publish publish) {
        publishDao.update(publish);

        return publish;
    }

    @GetMapping
    @ApiOperation(value = "列表")
    public PageTableResponse list(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return publishDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<Publish> list(PageTableRequest request) {

                request.getParams().putIfAbsent("orderBy", "  ORDER BY createTime DESC  ");

                return publishDao.list(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    public void delete(@PathVariable Long id) {
        publishDao.delete(id);
    }
}
