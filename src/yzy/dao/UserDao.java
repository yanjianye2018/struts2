package yzy.dao;

import cn.itcast.utils.JdbcUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import yzy.domain.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yzy
 */
public class UserDao {
    private QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());

    public User findByLoginnameAndLoginpass(String loginname, String loginpass) {
        try {
            String sql = "select * from t_user where loginname=? and loginpass=?";
            return qr.query(sql, new BeanHandler<User>(User.class), loginname, loginpass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAll() {
        try {
            String sql = "select * from t_user";
            return qr.query(sql, new BeanListHandler<User>(User.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void add(User user) {
        try {
            String sql = "insert into t_user values(?,?,?,?,?,?,?,?,?,?,?,?)";
            qr.update(sql, user.getUid(), user.getUsername(), user.getLoginname(),
                    user.getLoginpass(), user.getGender(), user.getBirthday(),
                    user.getEducation(), user.getCellphone(), user.getHobby(),
                    user.getFilepath(), user.getFilename(), user.getRemark());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> query(User user) {
        try {
            //给出sql语句
            String sql = "select * from t_user where 1=1 ";
            //创建集合添加sql语句
            List<Object> params = new ArrayList<>();
            StringBuilder sb = new StringBuilder(sql);
            String username = user.getUsername();
            if (username != null && !username.trim().isEmpty()) {
                sb.append("and username like ? ");
                params.add("%" + username + "%");
            }

            String gender = user.getGender();
            if (gender != null && !gender.trim().isEmpty()) {
                sb.append("and gender=? ");
                params.add(gender);
            }

            String education = user.getEducation();
            if (education != null && !education.trim().isEmpty()) {
                sb.append("and education=? ");
                params.add(education);
            }

            String upload = user.getUpload();
            if (upload != null && !upload.trim().isEmpty()) {
                if (upload.equals("yes")) {
                    sb.append("and filename is not null");
                } else if (upload.equals("no")) {
                    sb.append("and filename is null");
                }
            }
            return qr.query(sb.toString(), new BeanListHandler<User>(User.class), params.toArray());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String uid) {
        try {
            String sql = "delete from t_user where uid=?";
            qr.update(sql, uid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User load(String uid) {
        try {
            String sql = "select * from t_user where uid=?";
            return qr.query(sql, new BeanHandler<User>(User.class), uid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void edit(User user) {
        try {
            String sql = "update t_user set username=?,loginname=?,loginpass=?,gender=?," +
                    "birthday=?,education=?,cellphone=?,hobby=?,filepath=?,filename=?," +
                    "remark=? where uid=?";
            qr.update(sql, user.getUsername(), user.getLoginname(),
                    user.getLoginpass(), user.getGender(), user.getBirthday(),
                    user.getEducation(), user.getCellphone(), user.getHobby(),
                    user.getFilepath(), user.getFilename(), user.getRemark(),
                    user.getUid());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}

