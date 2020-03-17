package com.happyroad.wy.service.impl;

import com.happyroad.wy.dao.OrgDao;
import com.happyroad.wy.entity.Org;
import com.happyroad.wy.service.OrgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgServiceImpl implements OrgService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private OrgDao orgDao;

    @Override
    public void save(Org org) {
        orgDao.save(org);

        log.debug("新增机构{}", org.getOrgName());
    }

    @Override
    public void update(Org org) {
        orgDao.update(org);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Org> listAll() {
        return orgDao.listAll();
    }

    @Override
    public List<Org> listParents() {
        return null;
    }

    @Override
    public Org getById(Long id) {
        return null;
    }


    //@Transactional


}
