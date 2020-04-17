package com.yusheng.hbgj.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yusheng.hbgj.constants.UserConstants;
import com.yusheng.hbgj.dao.PermissionDao;
import com.yusheng.hbgj.dao.RoleDao;
import com.yusheng.hbgj.dto.RoleDto;
import com.yusheng.hbgj.entity.Permission;
import com.yusheng.hbgj.entity.Role;
import com.yusheng.hbgj.service.RedisService;
import com.yusheng.hbgj.service.RoleService;
import com.yusheng.hbgj.utils.UserUtil2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;


    @Autowired
    private RedisService redisService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(RoleDto roleDto) {

        Role role = roleDto;

        if (role.getId() != null) {   // 修改
            Role r = roleDao.getRole(role.getName());
            if (r != null && !(r.getId().equals(role.getId()))) {
                throw new IllegalArgumentException(role.getName() + "已存在");
            }

            roleDao.update(role);

            saveRolePermission(role.getId(), roleDto.getPermissionIds());


            // 立刻更新redis中的用户权限
            this.refreshPermissToRedis(role.getId());

            log.debug("修改角色{}", role.getName());


        } else {
            // 新增
            Role r = roleDao.getRole(role.getName());
            if (r != null) {
                throw new IllegalArgumentException(role.getName() + "已存在");
            }

            roleDao.save(role);

            saveRolePermission(role.getId(), roleDto.getPermissionIds());

            log.debug("新增角色{}", role.getName());
        }


    }

    /***
     * 通过最新的权限到Redis中去
     */
    private void refreshPermissToRedis(Long roleId) {


        Runnable runnable = new Runnable() {

            @Override
            public void run() {


                List<Permission> permissions = permissionDao.listByRoleId(roleId);

                Set<Object> roleKeys = redisService.keys(UserConstants.USER_ROLE + "*");


                roleKeys.forEach((kkeeyy) -> {

                    String key = (String) kkeeyy;


                    List<Object> rolesList = redisService.listRange(key, 0, -1);


                    rolesList.forEach((roleItem) -> {

                        Role ii = JSONObject.parseObject((String) roleItem, Role.class);

                        if (ii.getId().equals(roleId)) {


                            String permissKey = key.replace(UserConstants.USER_ROLE, UserConstants.USER_PERMISSION);

                            Long expireTime = redisService.getExpire(permissKey);

                            UserUtil2.setPermission(permissKey, permissions, expireTime);
                            log.info("刷新用户权限成功:{}", permissKey);

                        }


                    });


                });


            }
        };
        Thread thread = new Thread(runnable);
        thread.start();


    }

    private void saveRolePermission(Long roleId, List<Long> permissionIds) {
        roleDao.deleteRolePermission(roleId);
        permissionIds.remove(0L);
        if (!CollectionUtils.isEmpty(permissionIds)) {
            roleDao.saveRolePermission(roleId, permissionIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        roleDao.deleteRolePermission(id);
        roleDao.deleteRoleUser(id);
        roleDao.delete(id);

        log.debug("删除角色id:{}", id);
    }

}
