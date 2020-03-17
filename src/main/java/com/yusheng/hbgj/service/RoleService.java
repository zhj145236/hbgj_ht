package com.yusheng.hbgj.service;

import com.yusheng.hbgj.dto.RoleDto;

public interface RoleService {

	void saveRole(RoleDto roleDto);

	void deleteRole(Long id);
}
