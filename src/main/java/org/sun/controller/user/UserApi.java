package org.sun.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.sun.pojo.User;
import org.sun.response.ResponseResult;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    /**
     * 初始化管理员账号
     * @return
     */
    @PostMapping("/admin_account")
    public ResponseResult initManagerAccount(@RequestBody User user){
        log.info("user name == > " + user.getUser_name());
        log.info("password == > " + user.getPassword());
        log.info("email == > " + user.getEmail());
        return ResponseResult.SUCCESS();
    }

    /**
     * 注册
     * @param user
     * @return
     */
    @PostMapping
    public ResponseResult register(@RequestBody User user){
        return null;
    }

    /**
     * 登录
     * @param captcha
     * @param user
     * @return
     */
    @PostMapping("/{captcha}")
    public ResponseResult login(@PathVariable("captcha") String captcha, @RequestBody User user){
        return null;
    }

    /**
     * 获取图灵验证码
     * @return
     */
    @GetMapping("/captcha")
    public ResponseResult getCaptcha(){
        return null;
    }

    /**
     * 发送邮件
     * @param emailAddress
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(@RequestParam("email") String emailAddress){
        log.info("email == > " + emailAddress);
        return ResponseResult.SUCCESS();
    }

    /**
     * 修改密码
     * @param user
     * @return
     */
    @PutMapping("/password")
    public ResponseResult updatePassword(@RequestBody User user){
        return null;
    }

    /**
     * 获取作者信息
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId){
        return null;
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @PutMapping
    public ResponseResult updateUserInfo(@RequestBody User user){
        return null;
    }
}
