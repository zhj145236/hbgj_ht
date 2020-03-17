package com.happyroad.wy.service;

import com.happyroad.wy.entity.Permission;

public interface PermissionService {

	void save(Permission permission);

	void update(Permission permission);

	void delete(Long id);
}
