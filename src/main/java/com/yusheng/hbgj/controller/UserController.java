package com.yusheng.hbgj.controller;

import com.alibaba.fastjson.JSONObject;
import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.annotation.PermissionTag;
import com.yusheng.hbgj.constants.NotLoginException;
import com.yusheng.hbgj.constants.UserConstants;
import com.yusheng.hbgj.dao.NoticeDao;
import com.yusheng.hbgj.dao.UserDao;
import com.yusheng.hbgj.dto.UserDto;
import com.yusheng.hbgj.entity.Notice;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import com.yusheng.hbgj.service.FileService;
import com.yusheng.hbgj.service.RedisService;
import com.yusheng.hbgj.service.SysLogService;
import com.yusheng.hbgj.service.UserService;
import com.yusheng.hbgj.utils.DateUtil;
import com.yusheng.hbgj.utils.UserUtil2;
import com.yusheng.hbgj.vo.WeiXinVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 用户相关接口
 *
 * @author Jinwei
 */
@Api(tags = "用户")

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;


    @Value("${constants.hlgjId}")
    private String hlgjId;


    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private FileService fileService;


    @Value("${constants.companyRoleId}")
    private Long companyRoleId;

    @Autowired
    private NoticeDao noticeDao;


    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "Web保存用户", notes = "如果没有勾选任何角色，系统就默认设置为厂商角色")
    @PermissionTag("sys:user:add")
    public User saveUser(@RequestBody UserDto userDto) {

        User u = userService.getUser(userDto.getUsername());
        if (u != null) {
            throw new IllegalArgumentException(userDto.getUsername() + "已存在");
        }

        roleDeal(userDto, companyRoleId);

        if (userDto.getCompFlag() == null) {
            userDto.setCompFlag(1);
        }


        User newUser = userService.saveUser(userDto);


        // 如果有设置合同到期时间，就设置合同提醒
        if (userDto.getContractEndDate() != null) {

            this.addNotices(newUser);

        }
        return newUser;


    }


    private void addNotices(User newUser) {



        if (newUser.getId() == null) {
            return;
        }

        String title = "服务快到期提醒";
        int i1 = noticeDao.dropNotice(newUser.getId() + "", hlgjId, title);
        int i2 = noticeDao.dropNotice(newUser.getId() + "", newUser.getId() + "", title);

        if (newUser.getContractEndDate() == null) {
            return;
        }


        String endDateStr = DateUtil.dateFormat(newUser.getContractEndDate());

        // 30天前；7天前；1天前通知
        Date[] dates = new Date[]{DateUtil.addDay(newUser.getContractEndDate(), -30), DateUtil.addDay(newUser.getContractEndDate(), -7), DateUtil.addDay(newUser.getContractEndDate(), -1)};



        for (Date date : dates) {


            // 结束时间大约现在的
            if (DateUtil.daysBetween(new Date(), date) >= 0) {


                Notice notice = new Notice();
                notice.setIsPersonal(Notice.Personal.YES);
                notice.setTitle(title);
                notice.setReceiveId(hlgjId);

                // 给东莞市环联管家生态环境科技有限公司发通知
                String sb = "您与" + newUser.getNickname() + "签订的合同将于" + endDateStr + "到期,请及时处理哦。如已处理，请忽略本通知。";
                notice.setContent(sb);
                notice.setCreateName("系统程序");
                //设置合同到期前X天创建
                notice.setCreateTime(date);
                notice.setRefId(newUser.getId() + "");

                //草稿
                notice.setUpdateTime(new Date());
                notice.setStatus(Notice.Status.DRAFT);

                noticeDao.save(notice);


                // 给厂商也要发通知
                Notice notice2 = new Notice();
                notice2.setIsPersonal(Notice.Personal.YES);
                notice2.setTitle(title);

                //厂商ID
                notice2.setReceiveId(newUser.getId() + "");

                String sb2 = "您与东莞市环联管家生态环境科技有限公司签订的合同将于" + endDateStr + "到期,请联系他们处理哦。如已处理，请忽略本通知。";
                notice2.setContent(sb2);
                //草稿
                notice2.setCreateName("系统程序");
                notice2.setStatus(Notice.Status.DRAFT);

                notice2.setUpdateTime(new Date());
                //设置合同到期前X天创建
                notice2.setCreateTime(date);
                notice2.setRefId(newUser.getId() + "");


                noticeDao.save(notice2);


            }
        }
    }

    @LogAnnotation
    @PutMapping
    @ApiOperation(value = "修改用户")
    @PermissionTag("sys:user:add")
    public User updateUser(@RequestBody UserDto userDto, HttpSession session) {


        // 检测不与其他的用户名冲突
        User u = userService.getUser(userDto.getUsername());
        if (u != null && !u.getId().equals(userDto.getId())) {
            throw new IllegalArgumentException("此账号" + userDto.getUsername() + "已经被其他人使用，请重新填写");
        }


        User user = userService.updateUser(userDto, session);


        //  新增合同提醒，如果有设置合同提醒
        this.addNotices(user);


        return user;

    }


    @LogAnnotation
    @PutMapping(params = "headImgUrl")
    @ApiOperation(value = "修改头像")
    public void updateHeadImgUrl(String headImgUrl, HttpSession session) {
        User user = UserUtil2.getCurrentUser();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        userDto.setHeadImgUrl(headImgUrl);

        userService.updateUser(userDto, session);
        log.debug("{}修改了头像", user.getUsername());
    }


    @GetMapping("/getAllUser")
    @ApiOperation(value = "获取所有用户/厂商")
    public List<User> getAllUser() {

        return userService.getAllUser();
    }


    @LogAnnotation
    @PutMapping("/{username}")
    @ApiOperation(value = "修改密码")
    @PermissionTag("sys:user:password")
    public void changePassword(@PathVariable String username, String oldPassword, String newPassword) {

        userService.changePassword(username, oldPassword, newPassword);

    }


    @PutMapping("/agreeLicence")
    @ApiOperation(value = "厂商同意法律许可")
    @PermissionTag("sys:user:licence")
    public Boolean agreeLicence(@RequestParam String userId) {

        Boolean isSucess = userService.agreeLicence(userId);

        if (isSucess) {
            sysLogService.save(Long.parseLong(userId), "系统", true, "厂商同意了许可");
            return true;
        } else {

            return false;
        }


    }


    @GetMapping
    @ApiOperation(value = "用户列表")
    @PermissionTag("sys:user:query")
    public PageTableResponse listUsers(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {


            @Override
            public int count(PageTableRequest request) {

                // 默认值只显示厂商
                request.getParams().putIfAbsent("compFlag", "1");

                return userDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<User> list(PageTableRequest request) {

                // 默认值只显示厂商
                request.getParams().putIfAbsent("compFlag", "1");

                return userDao.list(request.getParams(), request.getOffset(), request.getLimit());

            }
        }).handle(request);
    }

    @ApiOperation(value = "当前登录用户")
    @GetMapping("/current")
    public User currentUser() {

        User tar = UserUtil2.getCurrentUser();

        User user = new User();
        BeanUtils.copyProperties(tar, user);
        user.setOriginalPassword(null);
        user.setPassword(null);
        return user;
    }

    @ApiOperation(value = "根据用户id获取用户")
    @GetMapping("/{id}")
    @PermissionTag("sys:user:query")
    public User user(@PathVariable Long id) {
        return userDao.getById(id);
    }


    @ApiOperation(value = "根据用户id删除用户")
    @DeleteMapping("/delete/{id}")
    @PermissionTag("sys:user:delete")
    public boolean delete(@PathVariable Long id) {


        // 删除用户
        userDao.delete(id);

        //用户下线
        Set<Object> keys = redisService.keys(UserConstants.LOGIN_TOKEN + "*");

        for (Object key : keys) {
            String s = redisService.get((String) key);
            User onlineUser = JSONObject.parseObject(s, User.class);
            if (onlineUser != null && id.equals(onlineUser.getId())) {
                redisService.delete((String) key);
                break;
            }
        }

        // 删未来的对应提醒,有就删
        String title = "服务快到期提醒";
        int i1 = noticeDao.dropNotice(id + "", hlgjId, title);
        int i2 = noticeDao.dropNotice(id + "", id + "", title);


        return true;

    }

    @ApiOperation(value = "判断能否删除用户")
    @GetMapping("/delAdvise/{id}")
    @PermissionTag("sys:user:delete")
    public String deleteAdvise(@PathVariable Long id) {


        //有无文档引用？
        StringBuilder sb = new StringBuilder();
        int i = fileService.fileRefCount(id + "");
        if (i >= 1) {
            sb.append("此用户关联了").append(i).append("个文档；");
        }

        //判断此用户有无在线？
        Set<Object> keys = redisService.keys(UserConstants.LOGIN_TOKEN + "*");

        for (Object key : keys) {

            String s = redisService.get((String) key);

            User onlineUser = JSONObject.parseObject(s, User.class);

            if (onlineUser != null && id.equals(onlineUser.getId())) {

                sb.append("此用户是登录在线状态；");
                break;
            }

        }

        if (sb.toString().length() == 0) {

            sb.append("此用户无文档关联，并且不在线，可以放心删除，");
        }

        sb.append("是否确认删除此账号？");
        return sb.toString();


    }


    @PostMapping("/tryLogin")
    @ApiOperation(value = "用户(游客或者厂商)尝试自动登录")
    public Map tryLogin(@RequestBody WeiXinVo wx) {


        if (StringUtils.isEmpty(wx.getOpenid())) {
            throw new IllegalArgumentException("openid参数丢失");
        }


        String token = redisService.get(UserConstants.OPENID_MAP_TOKEN + wx.getOpenid());

        String userStr;

        // token还在
        if (!StringUtils.isEmpty(token) && (userStr = redisService.get(UserConstants.LOGIN_TOKEN + token)) != null) {


            // 已存在账号并已登录
            Map<String, Object> maps = new HashMap<>();

            User loginUser = JSONObject.parseObject(userStr, User.class);

            loginUser.setPassword(null);
            loginUser.setOriginalPassword(null);
            maps.put("user", loginUser);
            maps.put("role", UserUtil2.getRole(token));
            maps.put("token", token);
            maps.put("message", "之前已经登录，无需再次登录");
            return maps;


        } else {

            throw new NotLoginException("因为您长期没有使用,会话已经过期,请重新登录");

        }


    }


    private void roleDeal(UserDto wx, Long roleId) {

        if (wx.getRoleIds() == null) {
            wx.setRoleIds(new ArrayList<>());
        }
        if (wx.getRoleIds().size() == 0) {
            ArrayList<Long> roles = new ArrayList<>(1);
            roles.add(roleId);
            wx.setRoleIds(roles);
        }
    }


}
