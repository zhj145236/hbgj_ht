package com.yusheng.hbgj.service;

import com.yusheng.hbgj.entity.SysLogs;

/**
 * 日志service
 * 
 * @author Jinwei
 *
 *         2017年8月19日
 */
public interface SysLogService {

	void save(SysLogs sysLogs);

	void save(Long userId, String module, Boolean flag, String remark);

	void deleteLogs();
}
