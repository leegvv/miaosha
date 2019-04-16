package net.arver.miaosha.service;

import net.arver.miaosha.dao.UserDao;
import net.arver.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User getById(final int id) {
        return userDao.getById(id);
    }

    @Transactional
    public boolean tx() {
        final User user1 = new User();
        user1.setId(2);
        user1.setName("lee");
        userDao.inset(user1);
        final User user2 = new User();
        user2.setId(1);
        user2.setName("sfs ");
        userDao.inset(user2);
        return true;
    }


}
