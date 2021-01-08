package org.sun.controller.user;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.sun.pojo.SobUser;
import org.sun.response.ResponseResult;
import org.sun.services.IUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    @PostMapping("/join_in")
    public ResponseResult register(@RequestBody SobUser sobUser,
                                   @RequestParam("email_code")String emailCode,
                                   @RequestParam("captcha_code") String captchaCode,
                                   @RequestParam("captchaKey") String captchaKey,
                                   HttpServletRequest request){
        return userService.register(sobUser, emailCode, captchaCode, captchaKey, request);
    }

    /**
     * 登录
     * <p>
     *     需要提交的数据
     *     1.用户账号-可以昵称，可以邮箱--->做了唯一处理
     *     2.密码
     *     3.图灵验证码
     *     4.图灵验证的key
     * @param captcha   图灵验证码
     * @param sobUser   用户bean类，封装着账号和密码
     * @param captchaKey   图灵验证码的key
     * @return
     */
    @PostMapping("/login/{captcha}/{captchaKey}")
    public ResponseResult login(@PathVariable("captchaKey") String captchaKey,
                                @PathVariable("captcha") String captcha,
                                @RequestBody SobUser sobUser,
                                HttpServletRequest request,
                                HttpServletResponse response){
        return userService.doLogin(captcha, captchaKey, sobUser, request, response);
    }



    /**
     * 获取图灵验证码
     * 有效时间10分钟
     * @return
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, @RequestParam("captcha_key")String captchaKey) {
        log.info("获取验证码。。。");
        try {
            userService.createCaptcha(response, captchaKey);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 发送邮件
     * 使用场景：注册、找回密码、修改邮箱(会输入新的邮箱)
     * 注册：如果已经注册过了，就提示说，该邮箱已经注册
     * 找回密码：如果没有注册过，提示该邮箱没有注册
     * 修改邮箱(新的邮箱)：如果已经注册了，提示该邮箱已经注册
     * @param emailAddress
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(HttpServletRequest request,@RequestParam("type") String  type,@RequestParam("email") String emailAddress){
        log.info("email == > " + emailAddress);
        return userService.sendEmail(type, request,emailAddress);
    }

    /**
     * 修改密码
     * 修改密码
     * 普通做法：通过旧密码对比来更新密码
     *  <p>
     * 找回密码：既可以找回密码，也可以修改密码
     * 发送验证码到邮箱/手机 --> 判断验证码是否正确来判断
     * 对应邮箱/手机号码所注册的账号是否属于你
     *  <p>
     *  步骤：
     *  1.用户填写邮箱
     *  2.用户获取验证码type=forget
     *  3.填写验证码
     *  4.填写新的密码
     *  5.提交数据
     *  <p>
     *  数据包括：
     *  1.邮箱和新密码
     *  2.验证码
     *  <p>
     *  如果验证码正确-->所用邮箱注册的账号就是你的，可以修改密码
     * @param sobUser
     * @return
     */
    @PutMapping("/password/{verifyCode}")
    public ResponseResult updatePassword(@PathVariable("verifyCode") String verifyCode,
                                         @RequestBody SobUser sobUser){
        return userService.updateUserPassword(verifyCode, sobUser);
    }

    /**
     * 获取作者信息
     * @param userId
     * @return
     */
    @GetMapping("/user_info/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId){
        return userService.getUserInfo(userId);
    }

    /**
     * 修改用户信息
     * <p>
     *     允许用户修改的内容
     *     1.头像
     *     2.用户名（唯一的）
     *     3.签名
     *     4.密码（单独修改）
     *     5.Email（唯一的，单独修改）
     *
     * @param sobUser
     * @return
     */
    @PutMapping("/user_info/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId,
                                         @RequestBody SobUser sobUser){
        return userService.updateUserInfo(userId, sobUser);
    }

    /**
     * 获取用户列表
     * 权限：管理员权限
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listUsers(@RequestParam("page")int page,
                                    @RequestParam("size")int size){
        return userService.listUsers(page, size);
    }

    /**
     * 删除用户
     * <p>
     *     需要管理员权限
     *     </>
     * @param userId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId")String userId){
        //判断当前操作的用户是谁
        //根据用户角色判断是否可以删除
        //通过注解的方式来控制权限
        return userService.deleteUserById(userId);
    }

    /**
     * 检查该Email是否已经注册
     * @param email 邮箱地址
     * @return  SUCCESS->已经注册了，FAILED->没有注册
     */
    @ApiResponses({
            @ApiResponse(code = 20000, message = "表示当前邮箱已经注册了"),
            @ApiResponse(code = 40000, message = "表示当前邮箱未注册")
    })
    @GetMapping("/email")
    public ResponseResult checkEmail(@RequestParam("email") String email){
        return userService.checkEmail(email);
    }

    /**
     * 检查该用户名是否已经注册
     * @param userName 用户名
     * @return  SUCCESS->已经注册了，FAILED->没有注册
     */
    @ApiResponses({
            @ApiResponse(code = 20000, message = "表示当前用户名已经注册了"),
            @ApiResponse(code = 40000, message = "表示当前用户名未注册")
    })
    @GetMapping("/user_name")
    public ResponseResult checkUserName(@RequestParam("userName") String userName){
        return userService.checkUserName(userName);
    }

    /**
     * 修改邮箱
     * 1.必须已经登录了
     * 2.新的邮箱没有注册过
     *
     * 用户步骤：
     * 1.已经登录
     * 2.输入新的邮箱地址
     * 3.获取验证码type=update
     * 4.输入验证码
     * 5.提交数据
     *
     * 需要提交的数据
     * 1、新的邮箱地址
     * 2、验证码
     * 3.其他信息可以从token中获取
     * @return
     */
    @PutMapping("/email")
    public ResponseResult updateEmail(@RequestParam("email") String email,
                                      @RequestParam("verify_code") String verifyCode){
        return userService.updateEmail(email, verifyCode);
    }

    /**
     * 退出登录
     * <p>
     * 拿到token_key
     * -> 删除redis里对应的token
     * -> 删除mysql里对应的refreshToken
     * -> 删除cookie里的token_key
     * @return
     */
    public ResponseResult logout(){
        return userService.doLogOut();
    }
}
