package com.yusheng.hbgj.service;

import com.yusheng.hbgj.entity.Permission;

public interface PermissionService {

	void save(Permission permission);

	void update(Permission permission);

	void delete(Long id);
}
