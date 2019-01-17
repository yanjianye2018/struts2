package yzy.service;

import yzy.dao.UserDao;
import yzy.domain.User;

import java.util.List;

/**
 * @author yzy
 */
public class UserService {
    private UserDao userDao = new UserDao();

    public User login(String loginname, String loginpass) {
        return userDao.findByLoginnameAndLoginpass(loginname, loginpass);
    }

    public List<User> findAll(){
        return userDao.findAll();
    }

    public void add(User user){
        userDao.add(user);
    }

    public List<User> query(User user){
        return userDao.query(user);
    }

    public void delete(String uid){
        userDao.delete(uid);
    }

    public User load(String uid) {
        return userDao.load(uid);

    }

    public void edit(User user){
        userDao.edit(user);
    }
}


