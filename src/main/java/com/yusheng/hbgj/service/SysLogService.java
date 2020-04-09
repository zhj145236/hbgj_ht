package com.yusheng.hbgj.service;

import com.yusheng.hbgj.entity.SysLogs;
import com.yusheng.hbgj.entity.User;

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

    void saveRestfulLogin(User user, SysLogs sysLogs);

}
