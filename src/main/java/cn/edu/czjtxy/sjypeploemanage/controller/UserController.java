package cn.edu.czjtxy.sjypeploemanage.controller;

import cn.edu.czjtxy.sjypeploemanage.common.Result;
import cn.edu.czjtxy.sjypeploemanage.entity.User;
import cn.edu.czjtxy.sjypeploemanage.service.UserService;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    private static final String JWT_KEY = "sjy-peploe-manage-secret-key";

    private String generateToken(User user) {
        return JWT.create()
                .setPayload("userId", user.getId())
                .setPayload("username", user.getUsername())
                .setKey(JWT_KEY.getBytes())
                .setExpiresAt(new Date(System.currentTimeMillis() + 86400000L))
                .sign();
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String nickname = params.get("nickname");

        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }

        User user = userService.register(username, password, nickname != null ? nickname : username);
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("token", generateToken(user));
        return Result.success("注册成功", data);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }

        User user = userService.login(username, password);
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("token", generateToken(user));
        return Result.success("登录成功", data);
    }

    @GetMapping("/current")
    public Result<Map<String, Object>> current(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || !JWTUtil.verify(token, JWT_KEY.getBytes())) {
            return Result.error(401, "未登录或token无效");
        }

        Long userId = Long.valueOf(JWTUtil.parseToken(token).getPayload("userId").toString());
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("createTime", user.getCreateTime());
        return Result.success(data);
    }

    @PutMapping("/update")
    public Result<Map<String, Object>> updateProfile(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || !JWTUtil.verify(token, JWT_KEY.getBytes())) {
            return Result.error(401, "未登录或token无效");
        }

        Long userId = Long.valueOf(JWTUtil.parseToken(token).getPayload("userId").toString());
        String nickname = params.get("nickname") != null ? params.get("nickname").toString() : null;
        String email = params.get("email") != null ? params.get("email").toString() : null;
        String phone = params.get("phone") != null ? params.get("phone").toString() : null;

        userService.updateProfile(userId, nickname, email, phone);

        User user = userService.getById(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("createTime", user.getCreateTime());
        return Result.success("更新成功", data);
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || !JWTUtil.verify(token, JWT_KEY.getBytes())) {
            return Result.error(401, "未登录或token无效");
        }

        Long userId = Long.valueOf(JWTUtil.parseToken(token).getPayload("userId").toString());
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return Result.error("原密码和新密码不能为空");
        }

        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", null);
    }
}