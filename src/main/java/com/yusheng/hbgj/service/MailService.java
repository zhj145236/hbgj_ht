package com.yusheng.hbgj.service;

import java.util.List;

import com.yusheng.hbgj.entity.Mail;

public interface MailService {

	void save(Mail mail, List<String> toUser);
}
