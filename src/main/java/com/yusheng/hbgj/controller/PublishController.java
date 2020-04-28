package com.yusheng.hbgj.controller;

import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.constants.BusinessException;
import com.yusheng.hbgj.dao.NoticeDao;
import com.yusheng.hbgj.dao.PublishDao;
import com.yusheng.hbgj.dao.UserDao;
import com.yusheng.hbgj.dto.PublishDto;
import com.yusheng.hbgj.entity.Notice;
import com.yusheng.hbgj.entity.Publish;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import com.yusheng.hbgj.service.RedisService;
import com.yusheng.hbgj.utils.DateUtil;
import com.yusheng.hbgj.utils.RegexUtils;
import com.yusheng.hbgj.utils.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "发布需求")
@RestController
@RequestMapping("/publishs")
public class PublishController {

    @Autowired
    private PublishDao publishDao;


    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private UserDao userDao;


    @Value("${constants.hlgjId}")
    private String hlgjId;

    @Autowired
    private RedisService redisUtil;


    private static final Logger log = LoggerFactory.getLogger("adminLogger");


    @GetMapping("/getReplyButUnreadCount")
    @ApiOperation(value = "返回用户未读的留言数量", notes = "条件:留言被平台回复且自己还没有看 的数量")
    public Integer getReplyButUnreadCount(@RequestParam(required = false) String openid, @RequestParam(required = false) Long userId) {

        if (StringUtils.isEmpty(openid) && StringUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("参数openid与userId必填一项");
        }

        Long userIdA = StringUtils.isEmpty(userId) ? userDao.getUserId(openid) : userId;



        if (userIdA == null || userIdA == 0) {
            return 0;
        } else {

            return  publishDao.getReplyButUnreadCount(userIdA);

        }

    }

    @GetMapping("/makePublishRead")
    @ApiOperation(value = "设置此条留言已读", notes = "传入publishId(主键) 即可")
    public Boolean makePublishRead(@RequestParam Long publishId) {


        Publish pp = new Publish();
        pp.setId(publishId);
        pp.setUserReadTime(new Date());


        return publishDao.update(pp) >= 1;


    }


    @PostMapping
    @ApiOperation(value = "保存留言（提交需求）", notes = "微信小程序游客或者厂商给公司发布留言; 后台已经限制5分钟内最多可以提交5次，主要是防止恶意频繁提交")
    public Publish save(@RequestBody Publish publish) {

        if (StringUtils.isEmpty(publish.getPublishContent())) {
            throw new IllegalArgumentException("发布留言不能为空");
        }

        String notNullId;
        if (publish.getUserId() != null) {
            notNullId = publish.getUserId() + "";
        } else {
            notNullId = publish.getOpenid();
        }

        if (notNullId == null) {
            throw new IllegalArgumentException("提交参数不完整:userId或者openid必填一样");
        } else if (redisUtil.listSize("publish:" + notNullId, -1) >= 5L) {
            throw new BusinessException("操作失败，因为您最近已经提交了多次留言，请稍后再试");
        } else if (!RegexUtils.checkMobile(publish.getTel())) {
            throw new BusinessException("手机格式不正确，请重新输入");
        }
        publish.setCreateTime(new Date());
        String userType;
        if (!StringUtils.isEmpty(publish.getUserId())) {

            userType = "厂商";
            User uu = userDao.getById(publish.getUserId());
            if (uu != null) {
                publish.setHeadPic(uu.getHeadImgUrl());
                publish.setNickName(uu.getNickname());
            }
        } else {
            publish.setUserId(userDao.getUserId(publish.getOpenid()));
            userType = "游客";
        }

        publish.setRemark(userType);

        publishDao.save(publish);

        // 给管理员单独 发送通知
        Notice notice = new Notice();
        StringBuilder sb = new StringBuilder();
        sb.append("有").append(userType).append("于 ").append(DateUtil.getNowStr()).append(" 给您留言，内容为：").append(publish.getPublishContent()).append(" <br/><br/>更多详情请在【用户留言】中查看");
        notice.setContent(sb.toString());
        notice.setTitle(userType + "留言");
        notice.setStatus(Notice.Status.PUBLISH);
        notice.setCreateTime(new Date());
        notice.setUpdateTime(new Date());
        notice.setIsPersonal(Notice.Personal.YES);
        notice.setReceiveId(hlgjId);

        //publish被插入后自动返回主键id
        notice.setRefId(publish.getId() + "");
        noticeDao.save(notice);

        redisUtil.leftPush("publish:" + notNullId, publish.getPublishContent());
        if (redisUtil.listSize("publish:" + notNullId, -1) == 1L) {
            redisUtil.expire("publish:" + notNullId, 5, TimeUnit.MINUTES);
        }

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

        if (StringUtils.isEmpty(entity.getPublishContent())) {
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


        Long userIdA = entity.getUserId();


        if (userIdA == null) {
            userIdA = userDao.getUserId(entity.getOpenid());
        }

        notice.setReceiveId(userIdA + "");

        if (!StringUtils.isEmpty(publish.getReply())) {

            noticeDao.save(notice);
        }


    }


    @GetMapping("/wxlist")
    @ApiOperation(value = "微信小程序里查看自己的发布列表", notes = "需要传入openid或者userId ,二选一")
    public PageTableResponse wxlist(PageTableRequest request) {


        Map<String, Object> params = request.getParams();

        String openid = (String) params.get("openid");
        String userId = (String) params.get("userId");


        log.debug("openid:{} , userId:{}", openid, userId);

        if (StringUtils.isEmpty(openid) && StringUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("参数openid与userId必填一项");
        }

        Long userIdA = StringUtils.isEmpty(userId) ? userDao.getUserId(openid) : Long.parseLong(userId);

        params.put("userId", userIdA);

        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return publishDao.count(params);
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<PublishDto> list(PageTableRequest request) {

                params.putIfAbsent("orderBy", "  ORDER BY createTime DESC  ");

                return publishDao.wxlist(params, request.getOffset(), request.getLimit());
            }
        }).handle(request);
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
