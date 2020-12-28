package org.sun.controller;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sun.pojo.Test;
import org.sun.response.ResponseResult;
import org.sun.response.ResponseState;
import org.sun.utils.Constants;
import org.sun.utils.RedisUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/hello")
    public ResponseResult helloWorld(){
        System.out.println("hello world...");
        return ResponseResult.SUCCESS().setData("hello world");
    }

    @PostMapping("/test-login")
    public ResponseResult testLogin(@RequestBody Test user){
        log.info("user name -== > " + user.getUserName());
        log.info("password -== > " + user.getPassword());
        ResponseResult loginSuccess = new ResponseResult(ResponseState.LOGIN_SUCCESS);
        return loginSuccess.setData(user);
    }

    @Autowired
    private RedisUtils redisUtils;
    //http://localhost:2020/test/captcha
    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        // specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        specCaptcha.setFont(Captcha.FONT_1);
        // 设置类型，纯数字、纯字母、字母数字混合
        //specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        String content = specCaptcha.text().toLowerCase();
        log.info("captcha content == > " + content);
        // 验证码存入session
        //request.getSession().setAttribute("captcha", content);
        redisUtils.set(Constants.User.KEY_CAPTCHA_CONTENT+"123456",content,60*10);

        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }
}
