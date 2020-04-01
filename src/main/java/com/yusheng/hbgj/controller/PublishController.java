package com.yusheng.hbgj.controller;

import java.util.Date;
import java.util.List;

import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.dao.NoticeDao;
import com.yusheng.hbgj.dao.PublishDao;
import com.yusheng.hbgj.entity.Notice;
import com.yusheng.hbgj.entity.Publish;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import com.yusheng.hbgj.utils.DateUtil;
import com.yusheng.hbgj.utils.StrUtil;
import com.yusheng.hbgj.utils.UserUtil;
import org.apache.commons.lang3.StringUtils;
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

    @Value("${constants.hlgjId}")
    private String hlgjId;


    @PostMapping
    @ApiOperation(value = "保存")
    public Publish save(@RequestBody Publish publish) {


        publish.setCreateTime(new Date());
        publishDao.save(publish);


        // 给管理员单独 发送通知
        Notice notice = new Notice();
        StringBuffer sb = new StringBuffer();

        sb.append("有用户于 ").append(DateUtil.getNowStr()).append(" 给您留言，内容为：").append(publish.getPublishContent()).append(" <br/><br/>更多详情请在【用户留言】中查看");
        notice.setContent(sb.toString());
        notice.setTitle("用户留言");
        notice.setStatus(Notice.Status.PUBLISH);
        notice.setCreateTime(new Date());
        notice.setUpdateTime(new Date());
        notice.setIsPersonal(Notice.Personal.YES);
        notice.setReceiveId(hlgjId);
        notice.setRefId(publish.getId() + ""); //自动返回主键
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

        publish.setUpdateTime(new Date());
        publishDao.update(publish);

        return publish;
    }

    @LogAnnotation
    @PutMapping("/reply")
    @ApiOperation(value = "回复用户留言")
    public Publish rely(@RequestBody Publish publish) {

        publish.setUpdateTime(new Date());
        publishDao.update(publish);

        // 回复通知
        this.noticeUser(publish);


        return publish;
    }


    //通知用户有管理员回复TA消息
    private void noticeUser(Publish publish) {


        Publish entity = publishDao.getById(publish.getId());

        if (StringUtils.isBlank(entity.getPublishContent())) {
            entity.setPublishContent("");
        }

        Notice notice = new Notice();
        notice.setStatus(Notice.Status.PUBLISH);
        notice.setCreateTime(new Date());
        notice.setCreateTime(new Date());
        notice.setUpdateTime(new Date());
        notice.setIsPersonal(Notice.Personal.YES);
        notice.setTitle("新消息回复提醒");
        notice.setContent("您的留言【" + StrUtil.elide(entity.getPublishContent(), 10) + "】已被回复，请在【我的发布】里面查看");
        notice.setRefId(publish.getId().toString());
        notice.setReceiveId(entity.getOpenid());

        if (StringUtils.isNoneBlank(publish.getReply())) {

            noticeDao.save(notice);
        }


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
