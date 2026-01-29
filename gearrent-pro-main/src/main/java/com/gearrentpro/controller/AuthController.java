package com.gearrentpro.controller;

import com.gearrentpro.dao.UserDao;
import com.gearrentpro.db.Tx;
import com.gearrentpro.entity.UserSession;

public class AuthController {
    private final UserDao userDao = new UserDao();

    public UserSession login(String username, String password) {
        return Tx.inTx(con -> userDao.login(con, username, password));
    }
}
