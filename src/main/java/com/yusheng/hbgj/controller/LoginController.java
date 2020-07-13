package com.yusheng.hbgj.controller;

import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.constants.BusinessException;
import com.yusheng.hbgj.constants.UserConstants;
import com.yusheng.hbgj.dto.ResponseInfo;
import com.yusheng.hbgj.dto.UserDto;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.service.RedisService;
import com.yusheng.hbgj.service.UserService;
import com.yusheng.hbgj.utils.StrUtil;
import com.yusheng.hbgj.utils.SysUtil;
import com.yusheng.hbgj.utils.UserUtil2;
import com.yusheng.hbgj.vo.WeiXinVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录相关接口
 *
 * @author Jinwei
 */
@Api(tags = "登录")
@RestController
@RequestMapping
public class LoginController {


    @Autowired
    private UserService userService;


    @Value("${token.expire.webExpireDay}")
    private Integer webExpireDay;


    @Value("${token.expire.day}")
    private Integer expireDay;


    @Autowired
    private RedisService redisService;

    @Value("${constants.visitorRoleId}")
    private Long visitorRoleId;


    private static final Logger log = LoggerFactory.getLogger("adminLogger");


    @LogAnnotation
    @ApiOperation(value = "web端登录", notes = "传入账号和密码")
    @PostMapping("/sys/login")
    public void login(String username, String password, HttpServletRequest request, HttpSession session) {

        String key = "user:login_failed:" + username;


        String failCountStr = redisService.get(key);


        if (failCountStr != null && Long.parseLong(failCountStr) > 8L) {

            throw new BusinessException("您之前已经连续输错密码超过5次了,请稍后再试");
        }

        User user = userService.getUser(username);
        if (user != null) {


            if (user.getPassword().equals(userService.passwordEncoder(password, user.getSalt()))) {


                if (User.Status.VALID != user.getStatus()) {
                    log.warn("{},{}被锁定无法正常登录", user.getUsername(), user.getNickname());
                    throw new BusinessException("您的账号被锁住,如需登录请联系管理员");
                }

                String aa = user.getOriginalPassword();
                String bb = user.getPassword();
                userService.login(user, request, session);

                // Web登录
                user.setOriginalPassword(aa);
                user.setPassword(bb);
                log.info("{},{}", aa, bb);
                session.setAttribute(UserConstants.WEB_SESSION_KEY, user);
                session.setMaxInactiveInterval(webExpireDay * 3600 * 24);  // 秒为单位,这里设置30天


                log.info("登录成功 {},{},{}", user.getId(), user.getUsername(), user.getNickname());


            } else {

                String msg = this.dealLogin(username);
                log.info("登录失败，不正确的密码{}; {}", password, msg);
                throw new BusinessException(msg);

            }


        } else {

            // 不存在的账号
            log.info("不存在的账号{}", username);


            throw new BusinessException("登录失败：不存在的账号");

        }


    }

    // 连续输入3此密码错误 先锁住账号
    private String dealLogin(String username) {

        String key = "user:login_failed:" + username;

        if (redisService.get(key) == null) {

            redisService.set(key, "1");
            redisService.expire(key, 10, TimeUnit.MINUTES);

        } else {

            redisService.increment(key, 1);

        }

        int count = Integer.parseInt(redisService.get(key));


        if (count >= 8) {

            return ("您连续输错密码已经超过5次了,请稍后再试");

        } else if (count > 3) {

            return ("密码错误,你还有" + (8 - count) + "次机会尝试");

        } else {
            return "密码不正确，请重新输入";
        }
    }


    @ApiOperation(value = "Restful方式登录,前后端分离时登录接口", notes = "注意返回的token要放在RequestHeader上,后期调用其他接口都需要token")
    @PostMapping("/sys/login/restful")
    public ResponseInfo restfulLogin(@RequestParam String username, @RequestParam String password, @RequestParam String openid, HttpServletRequest request, HttpSession session) {


        if (SysUtil.paramsIsNull(username, password, openid)) {
            throw new IllegalArgumentException("账号,密码,openid必填");
        }

        String key = "user:login_failed:" + username;

        String failCountStr = redisService.get(key);

        if (failCountStr != null && Long.parseLong(failCountStr) > 8L) {

            throw new BusinessException("您之前已经连续输错密码超过5次了,请稍后再试");
        }

        User user = userService.getUser(username);

        if (user != null) {

            if (user.getPassword().equals(userService.passwordEncoder(password, user.getSalt()))) {


                if (User.Status.VALID != user.getStatus()) {
                    log.warn("{},{}被锁定无法正常登录", user.getUsername(), user.getNickname());
                    throw new BusinessException("您的账号已失效,如需登录请联系客服");
                }


                user.setOpenid(openid);


                // 账户关联上此用户的openid
                userService.saveOpenid(user.getId(), openid);


                // restful 登录
                Map<String, Object> maps = userService.login(user, request, session);

                log.info("登录成功 {},{},{}", user.getId(), user.getUsername(), user.getNickname());


                // TOKEN 保存XX天
                redisService.setForTimeCustom(UserConstants.OPENID_MAP_TOKEN + openid, (String) maps.get("token"), expireDay, TimeUnit.DAYS);


                return ResponseInfo.success(maps);

            } else {

                log.info("登录失败，{}输入了不正确的密码{}", username, password);
                String msg = this.dealLogin(username);
                throw new BusinessException(msg);

            }


        } else {

            log.info("登录失败,账号不存在，{}", username);
            throw new BusinessException("登录失败：账号不存在");


        }


    }


    @PostMapping("/visitorLogin")
    @ApiOperation(value = "游客通过微信授权登录")
    public Map visitorLogin(@RequestBody WeiXinVo wx, HttpServletRequest request, HttpServletResponse response, HttpSession session) {


        if (StringUtils.isEmpty(wx.getOpenid())) {
            throw new IllegalArgumentException("openid参数丢失");
        }


        User infoByOpenId = userService.getInfoByOpenId(wx.getOpenid(), 0);


        // 已注册过
        if (infoByOpenId == null) {


            // 未注册过
            log.info("微信新用户注册。。。。。{}", wx.getOpenid());
            UserDto userVo = new UserDto();

            // 默认加上游客角色
            List<Long> roles = new ArrayList<>(1);
            roles.add(visitorRoleId);
            userVo.setRoleIds(roles);


            userVo.setNickname(wx.getNickName());
            userVo.setHeadImgUrl(wx.getAvatarUrl());
            userVo.setAddress((wx.getCountry() == null ? "" : wx.getCountry()) + (wx.getProvince() == null ? "" : wx.getProvince()) + (wx.getCity() == null ? "" : wx.getCity()));
            userVo.setOpenid(wx.getOpenid());

            userVo.setSex(wx.getGender());
            userVo.setTelephone(wx.getTel());


            // 无法获取到用户微信名字，所以采用系统随机生成username
            String username = "wx" + StrUtil.random(8);
            while (userService.getUser(username) != null) {
                log.info(username + "已经被注册，系统重新选号码");
                username = "wx" + StrUtil.random(8);

            }
            userVo.setUsername(username);
            userVo.setRemark("系统自动为微信游客注册此账号");


            //设置初始密码
            String password = "123456";

            userVo.setPassword(password);
            userVo.setOriginalPassword(password);

            //非厂商
            userVo.setCompFlag(0);

            userVo.setStatus(User.Status.VALID);

            User user = userService.saveUser(userVo);

            // 为新用户在后台静默登录
            Map<String, Object> map = userService.login(user, request, session);

            map.put("message", "微信新用户登录");


            // TOKEN 保存XX天
            redisService.setForTimeCustom(UserConstants.OPENID_MAP_TOKEN + user.getOpenid(), (String) map.get("token"), expireDay, TimeUnit.DAYS);


            return map;


        } else {

            Map<String, Object> map = userService.login(infoByOpenId, request, session);
            map.put("message", "之前已授权的微信用户重新登录");

            // TOKEN 保存XX天
            redisService.setForTimeCustom(UserConstants.OPENID_MAP_TOKEN + infoByOpenId.getOpenid(), (String) map.get("token"), expireDay, TimeUnit.DAYS);

            return map;
        }


    }


    @ApiOperation(value = "获取当前登录用户")
    @GetMapping("/sys/login")
    public User getLoginInfo() {

        return UserUtil2.getCurrentUser();
    }


    @ApiOperation(value = "用户退出登录")
    @GetMapping("/sys/logout")
    public boolean logout(HttpServletRequest request, HttpSession session) {

        userService.logout(request, session);


        return true;

    }


}
