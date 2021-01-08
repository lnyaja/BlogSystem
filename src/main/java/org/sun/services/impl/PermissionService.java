package org.sun.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.sun.pojo.SobUser;
import org.sun.services.IUserService;
import org.sun.utils.Constants;
import org.sun.utils.CookieUtils;
import org.sun.utils.TextUtils;

import javax.servlet.http.HttpServletRequest;

@Service("permission")
public class PermissionService {


    @Autowired
    private IUserService userService;

    /**
     * 判断是不是管理员
     * @return
     */
    public boolean admin(){
        //拿到request和response
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String tokenKey = CookieUtils.getCookie(request, Constants.User.COOKIE_TOKEN_KEY);
        //没有令牌的key，没有登录，就不用往下执行了
        if (TextUtils.isEmpty(tokenKey)) {
            return false;
        }
        SobUser sobUser = userService.checkSobUser();
        if (sobUser == null) {
            return false;
        }
        if (Constants.User.ROLE_ADMIN.equals(sobUser.getRoles())){
            //管理员
            return true;
        }
        return false;
    }
}
