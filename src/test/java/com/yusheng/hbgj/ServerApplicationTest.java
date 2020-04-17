package com.yusheng.hbgj;

import com.yusheng.hbgj.config.InitConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerApplicationTest {




    @Test
    public void test0() {

      String aa=  InitConfig.globalConfig.get("devEmial");

        System.out.println(aa+"888//////////");

    }


}
