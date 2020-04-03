package com.yusheng.hbgj.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.dao.NoticeDao;
import com.yusheng.hbgj.dao.UserDao;
import com.yusheng.hbgj.dto.NoticeDto;
import com.yusheng.hbgj.dto.NoticeReadVO;
import com.yusheng.hbgj.dto.NoticeVO;
import com.yusheng.hbgj.entity.Notice;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import com.yusheng.hbgj.utils.SysUtil;
import com.yusheng.hbgj.utils.UserUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import sun.nio.cs.ext.MacArabic;

@Api(tags = "公告")
@RestController
@RequestMapping("/notices")
public class NoticeController {

    @Autowired
    private NoticeDao noticeDao;


    @Autowired
    private UserDao userDao;

    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "保存公告")
    @RequiresPermissions("notice:add")
    public Notice saveNotice(@RequestBody Notice notice) {

        notice.setIsPersonal(Notice.Personal.NO);
        noticeDao.save(notice);

        return notice;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取公告")
    @RequiresPermissions("notice:query")
    public Notice get(@PathVariable Long id) {
        return noticeDao.getById(id);
    }

    @GetMapping(params = "id")
    public NoticeVO readNotice(Long id) {
        NoticeVO vo = new NoticeVO();

        Notice notice = noticeDao.getById(id);
        if (notice == null || notice.getStatus() == Notice.Status.DRAFT) {
            return vo;
        }
        vo.setNotice(notice);

        noticeDao.saveReadRecord(notice.getId(), UserUtil.getCurrentUser().getId());

        List<User> users = noticeDao.listReadUsers(id);
        vo.setUsers(users);

        return vo;
    }

    @LogAnnotation
    @PutMapping
    @ApiOperation(value = "修改公告")
    @RequiresPermissions("notice:add")
    public Notice updateNotice(@RequestBody Notice notice) {
        Notice no = noticeDao.getById(notice.getId());
        if (no.getStatus() == Notice.Status.PUBLISH) {
            throw new IllegalArgumentException("发布状态的不能修改");
        }
        noticeDao.update(notice);

        return notice;
    }

    @GetMapping
    @ApiOperation(value = "公告管理列表")
    @RequiresPermissions("notice:query")
    public PageTableResponse listNotice(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return noticeDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<Notice> list(PageTableRequest request) {
                return noticeDao.list(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @LogAnnotation
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除公告")
    @RequiresPermissions(value = {"notice:del"})
    public void delete(@PathVariable Long id) {
        noticeDao.delete(id);
    }

    @ApiOperation(value = "未读公告数")
    @GetMapping("/count-unread")
    public Integer countUnread() {
        User user = UserUtil.getCurrentUser();
        return noticeDao.countUnread(user.getId());
    }

    @ApiOperation(value = "微信端用户获取未读公告数", notes = "一定要传入openid")
    @GetMapping("/wx_count_unread")
    public Integer wxcountUnread(@RequestParam String openid) {

        if (StringUtils.isEmpty(openid)) {
            throw new IllegalArgumentException("参数openid缺失");
        }
        Long userId = userDao.getUserId(openid);
        if (userId == null || userId == 0) {
            return 0;
        } else {
            return noticeDao.countUnread(userId);
        }
    }


    @GetMapping("/published")
    @ApiOperation(value = "公告列表")
    public PageTableResponse listNoticeReadVO(PageTableRequest request) {

        request.getParams().put("userId", UserUtil.getCurrentUser().getId());

        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return noticeDao.countNotice(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<NoticeReadVO> list(PageTableRequest request) {
                return noticeDao.listNotice(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @GetMapping("/wx_notice")
    @ApiOperation(value = "微信小程序端 提醒事项列表", notes = "已按时间降序显示，不管有没有阅读都会查询出来")
    public PageTableResponse wxlistNoticeReadVO(PageTableRequest request) {

        String openid = (String) request.getParams().get("openid");

        if (StringUtils.isEmpty(openid)) {

            throw new IllegalArgumentException("提交参数不完整");

        }

        request.getParams().put("userId", userDao.getUserId(openid));

        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return noticeDao.countNotice(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<NoticeDto> list(PageTableRequest request) {
                return noticeDao.wxlistNotice(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }


}
