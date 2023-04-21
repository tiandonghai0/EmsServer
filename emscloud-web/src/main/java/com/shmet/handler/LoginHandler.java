package com.shmet.handler;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.shmet.bean.LoginInfo;
import com.shmet.dao.TUserDao;
import com.shmet.entity.mysql.gen.TUser;

@Service
public class LoginHandler {
    @Resource
    TUserDao userDao;

    public List<TUser> getUser(LoginInfo li) {
        TUser param = new TUser();
        param.setAccount(li.getAccount());
        param.setPassword(li.getPassword());
        return userDao.select(param);
    }
}
