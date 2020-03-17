package com.happyroad.wy.service;

import com.happyroad.wy.entity.Org;

import java.util.List;

public interface OrgService {

	void save(Org permission);

	void update(Org permission);

	void delete(Long id);

    List<Org> listAll();

    List<Org> listParents();

    Org getById(Long id);
}
