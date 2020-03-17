package com.happyroad.wy.service;

import java.util.List;

import com.happyroad.wy.entity.Mail;

public interface MailService {

	void save(Mail mail, List<String> toUser);
}
