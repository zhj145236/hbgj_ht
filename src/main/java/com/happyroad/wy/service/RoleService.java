package com.happyroad.wy.service;

import com.happyroad.wy.dto.RoleDto;

public interface RoleService {

	void saveRole(RoleDto roleDto);

	void deleteRole(Long id);
}
