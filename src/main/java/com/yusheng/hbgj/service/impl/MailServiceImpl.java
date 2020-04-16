package com.yusheng.hbgj.service.impl;

import com.yusheng.hbgj.dao.MailDao;
import com.yusheng.hbgj.entity.Mail;
import com.yusheng.hbgj.service.MailService;
import com.yusheng.hbgj.service.SendMailSevice;
import com.yusheng.hbgj.utils.UserUtil2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MailServiceImpl implements MailService {

	private static final Logger log = LoggerFactory.getLogger("adminLogger");

	@Autowired
	private SendMailSevice sendMailSevice;
	@Autowired
	private MailDao mailDao;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(Mail mail, List<String> toUser) {
		mail.setUserId(UserUtil2.getCurrentUser().getId());
		mailDao.save(mail);

		toUser.forEach(u -> {
			int status = 1;
			try {
				sendMailSevice.sendMail(u, mail.getSubject(), mail.getContent());
			} catch (Exception e) {
				log.error("发送邮件失败", e);
				status = 0;
			}

			mailDao.saveToUser(mail.getId(), u, status);
		});

	}

}
