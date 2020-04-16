package com.yusheng.hbgj.dto;

import java.io.Serializable;
import java.util.List;

import com.yusheng.hbgj.entity.Notice;
import com.yusheng.hbgj.entity.User;

public class NoticeVO implements Serializable {

	private static final long serialVersionUID = 7363353918096951799L;

	private Notice notice;

	private List<User> users;

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

    @Override
    public String toString() {
        return "NoticeVO{}";
    }
}
