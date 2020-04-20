package com.yusheng.hbgj;

import com.yusheng.hbgj.utils.MysqlDbService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerApplicationTest {


    @Autowired
    private MysqlDbService mysqlDbService;

    @Test
    public void test0() {


        mysqlDbService.dbBackUp();
        System.out.println("备份成功！");

        //mysqlDbService. ();


    }


}
