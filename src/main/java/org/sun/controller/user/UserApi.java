package org.sun.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sun.pojo.SobUser;
import org.sun.response.ResponseResult;
import org.sun.services.IUserService;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    @Autowired
    private IUserService userService;

    /**
     * 初始化管理员账号
     * @return
     */
    @PostMapping("/admin_account")
    public ResponseResult initManagerAccount(@RequestBody SobUser sobUser, HttpServletRequest request){
        log.info("user name == > " + sobUser.getUserName());
        log.info("password == > " + sobUser.getPassword());
        log.info("email == > " + sobUser.getEmail());
        return userService.initManagerAccount(sobUser,request);
    }

    /**
     * 注册
     * @param sobUser
     * @return
     */
    @PostMapping
    public ResponseResult register(@RequestBody SobUser sobUser){
        return null;
    }

    /**
     * 登录
     * @param captcha
     * @param sobUser
     * @return
     */
    @PostMapping("/{captcha}")
    public ResponseResult login(@PathVariable("captcha") String captcha, @RequestBody SobUser sobUser){
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
     * @param sobUser
     * @return
     */
    @PutMapping("/password/{userId}")
    public ResponseResult updatePassword(@PathVariable("userId") String userId,@RequestBody SobUser sobUser){
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
     * @param sobUser
     * @return
     */
    @PutMapping("/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId,@RequestBody SobUser sobUser){
        return null;
    }

    @GetMapping("/list")
    public ResponseResult listUsers(@RequestParam("page")int page, @RequestParam("size")int size){
        return null;
    }

    @DeleteMapping("/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId")String userId){
        return null;
    }
}
