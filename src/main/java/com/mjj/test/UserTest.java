package com.mjj.test;

import com.mjj.model.User;
import com.mjj.util.RemoteCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miaojj on 2017/2/23.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context/spring-context.xml")
public class UserTest {

    @Autowired
    RemoteCache remoteCache;

    @Test
    public void  testAddUser(){

        User u1 = new User();
        User u2 = new User();
        u1.setId("1111");
        u1.setName("xiao");
        u2.setId("2222");
        u2.setName("da");
        List<User> list = new ArrayList<>();
        list.add(u1);
        list.add(u2);

        //放入缓存
        remoteCache.putList("aaa",list);

        //读取缓存
        List<User> users = remoteCache.getList("aaa",User.class);

        for(User u : users){
            System.out.println(u.getId()+"ID");
        }
        System.out.println(users.size()+"SIZE");

        users.clear();
        System.out.println(users.size()+"LAST");
        for(User r : users){
            System.out.println(r.getId());
        }
    }


}
