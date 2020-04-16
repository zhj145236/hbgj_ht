package com.yusheng.hbgj.service.impl;

import java.util.List;

import com.yusheng.hbgj.dao.RoleDao;
import com.yusheng.hbgj.dto.RoleDto;
import com.yusheng.hbgj.entity.Role;
import com.yusheng.hbgj.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private RoleDao roleDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(RoleDto roleDto) {
        Role role = roleDto;

        if (role.getId() != null) {// 修改
            Role r = roleDao.getRole(role.getName());
            if (r != null && !(r.getId().equals(role.getId()))) {
                throw new IllegalArgumentException(role.getName() + "已存在");
            }

            roleDao.update(role);
        } else {// 新增
            Role r = roleDao.getRole(role.getName());
            if (r != null) {
                throw new IllegalArgumentException(role.getName() + "已存在");
            }

            roleDao.save(role);

            log.debug("新增角色{}", role.getName());
        }

        saveRolePermission(role.getId(), roleDto.getPermissionIds());
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
