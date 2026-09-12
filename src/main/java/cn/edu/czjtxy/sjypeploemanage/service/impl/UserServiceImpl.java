package cn.edu.czjtxy.sjypeploemanage.service.impl;

import cn.edu.czjtxy.sjypeploemanage.entity.User;
import cn.edu.czjtxy.sjypeploemanage.mapper.UserMapper;
import cn.edu.czjtxy.sjypeploemanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    @Override
    public User register(String username, String password, String nickname) {
        User existUser = userMapper.selectByUsername(username);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setNickname(nickname);
        userMapper.insert(user);
        return user;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void updateProfile(Long id, String nickname, String email, String phone) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        userMapper.update(user);
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String oldMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!oldMd5.equals(user.getPassword())) {
            throw new RuntimeException("原密码不正确");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        userMapper.update(user);
    }
}