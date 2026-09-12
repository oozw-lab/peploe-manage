package cn.edu.czjtxy.sjypeploemanage.service;

import cn.edu.czjtxy.sjypeploemanage.entity.User;

public interface UserService {

    User login(String username, String password);

    User register(String username, String password, String nickname);

    User getById(Long id);

    void updateProfile(Long id, String nickname, String email, String phone);

    void changePassword(Long id, String oldPassword, String newPassword);
}