package com.yusheng.hbgj.controller;

import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.annotation.PermissionTag;
import com.yusheng.hbgj.constants.NotLoginException;
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
import com.yusheng.hbgj.utils.UserUtil2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

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
    @PermissionTag("notice:add")
    public Notice saveNotice(@RequestBody Notice notice) {

        notice.setIsPersonal(Notice.Personal.NO);
        noticeDao.save(notice);

        return notice;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取公告")
    @PermissionTag("notice:query")
    public Notice get(@PathVariable Long id) {
        return noticeDao.getById(id);
    }

    @GetMapping(params = "id")
    public NoticeVO readNotice(Long id, HttpSession session) {
        NoticeVO vo = new NoticeVO();

        Notice notice = noticeDao.getById(id);
        if (notice == null || notice.getStatus() == Notice.Status.DRAFT) {
            return vo;
        }
        vo.setNotice(notice);

        User currentUser = UserUtil2.getCurrentUser();

        if (currentUser != null) {

            noticeDao.saveReadRecord(notice.getId(), currentUser.getId());

            List<User> users = noticeDao.listReadUsers(id);
            vo.setUsers(users);
        }


        return vo;
    }

    @LogAnnotation
    @PutMapping
    @ApiOperation(value = "修改公告")
    @PermissionTag("notice:add")
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
    @PermissionTag("notice:query")
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
    @PermissionTag(value = {"notice:del"})
    public void delete(@PathVariable Long id) {
        noticeDao.delete(id);
    }

    @ApiOperation(value = "未读公告数")
    @GetMapping("/count-unread")
    public Integer countUnread() {

        User user = UserUtil2.getCurrentUser();
        if (user == null) {
            throw new NotLoginException();
        }
        return noticeDao.countUnread(user.getId());
    }

    @ApiOperation(value = "微信端用户获取未读公告数", notes = "openid和userId二选一")
    @GetMapping("/wx_count_unread")
    public Integer wxcountUnread(@RequestParam(required = false) String openid, @RequestParam(required = false) String userId) {

        if (StringUtils.isEmpty(openid) && StringUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("参数openid与userId必填一项");
        }

        String userIdA = StringUtils.isEmpty(userId) ? userDao.getUserId(openid) + "" : userId;

        if ("null".equals(userIdA)) {
            return 0;
        } else {
            return noticeDao.countUnread(Long.parseLong(userIdA));
        }
    }

    @ApiOperation(value = "微信端用户已读完此条消息", notes = "一定要传入noticeId ; openid和userId 二选一")
    @GetMapping("/wxHasRead")
    public Boolean wxHasRead(@RequestParam Long noticeId, @RequestParam(required = false) String openid, @RequestParam(required = false) String userId) {

        if (StringUtils.isEmpty(openid) && StringUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("参数openid与userId必填一项");
        }

        String userIdA = StringUtils.isEmpty(userId) ? userDao.getUserId(openid) + "" : userId;

        if ("null".equals(userIdA)) {
            return true;
        } else {
            noticeDao.saveReadRecord(noticeId, Long.parseLong(userIdA));
            return true;
        }

    }


    @GetMapping("/published")
    @ApiOperation(value = "公告列表")
    public PageTableResponse listNoticeReadVO(PageTableRequest request) {

        request.getParams().put("userId", UserUtil2.getCurrentUser().getId());

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
    @ApiOperation(value = "微信小程序端的提醒事项列表", notes = "已按时间降序显示，不管有没有阅读都会查询出来")
    public PageTableResponse wxlistNoticeReadVO(PageTableRequest request) {

        String openid = (String) request.getParams().get("openid");
        String userId = (String) request.getParams().get("userId");

        if (StringUtils.isEmpty(openid) && StringUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("参数openid与userId必填一项");
        }


        String userIdA = StringUtils.isEmpty(userId) ? userDao.getUserId(openid) + "" : userId;

        request.getParams().put("userId", userIdA);

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
