package org.sun.services;

import org.sun.pojo.SobUser;
import org.sun.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;

public interface IUserService {

    ResponseResult initManagerAccount(SobUser sobUser, HttpServletRequest request);
}
