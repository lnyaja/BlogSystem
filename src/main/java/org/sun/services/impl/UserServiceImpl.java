package org.sun.services.impl;

import com.google.gson.Gson;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.sun.dao.RefreshTokenDao;
import org.sun.dao.SettingsDao;
import org.sun.dao.UserDao;
import org.sun.pojo.RefreshToken;
import org.sun.pojo.Setting;
import org.sun.pojo.SobUser;
import org.sun.response.ResponseResult;
import org.sun.response.ResponseState;
import org.sun.services.IUserService;
import org.sun.utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements IUserService {

   @Autowired
   private IdWorker idWorker;

   @Autowired
   private BCryptPasswordEncoder bCryptPasswordEncoder;

   @Autowired
   private UserDao userDao;

   @Autowired
   private SettingsDao settingsDao;

   @Autowired
   private RefreshTokenDao refreshTokenDao;

   @Autowired
   private Gson gson;

    @Override
    public ResponseResult initManagerAccount(SobUser sobUser, HttpServletRequest request) {
        //检查是否初始化
        Setting managerAccountState = settingsDao.findByKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        if (managerAccountState != null) {
            return ResponseResult.FAILED("管理员账号已经初始化了");
        }

        //检查数据
        if (TextUtils.isEmpty(sobUser.getUserName())) {
            return ResponseResult.FAILED("用户名不能为空");
        }
        if (TextUtils.isEmpty(sobUser.getPassword())) {
            return ResponseResult.FAILED("密码不能为空");
        }
        if (TextUtils.isEmpty(sobUser.getEmail())) {
            return ResponseResult.FAILED("邮箱不能为空");
        }
        //补充数据
        sobUser.setId(String.valueOf(idWorker.nextId()));
        sobUser.setRoles(Constants.User.ROLE_ADMIN);
        sobUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        sobUser.setState(Constants.User.DEFAULT_STATE);
        String remoteAddr = request.getRemoteAddr();
        String localAddr = request.getLocalAddr();
        log.info("remoteAddr == >"+ remoteAddr);
        log.info("localAddr == >"+ localAddr);
        sobUser.setLoginIp(remoteAddr);
        sobUser.setRegIp(remoteAddr);
        sobUser.setCreateTime(new Date());
        sobUser.setUpdateTime(new Date());
        //对密码进行加密
        //原密码
        String password = sobUser.getPassword();
        //加密
        String encode = bCryptPasswordEncoder.encode(password);
        sobUser.setPassword(encode);
        //保存到数据库里
        userDao.save(sobUser);
        //更新已经添加的标记
        Setting setting = new Setting();
        setting.setId(idWorker.nextId()+"");
        setting.setKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        setting.setCreateTime(new Date());
        setting.setUpdateTime(new Date());
        setting.setValue("1");
        settingsDao.save(setting);
        return ResponseResult.SUCCESS("初始化成功");
    }

    public static final int[] captcha_font_types={Captcha.FONT_1,
            Captcha.FONT_2,
            Captcha.FONT_3,
            Captcha.FONT_4,
            Captcha.FONT_5,
            Captcha.FONT_6,
            Captcha.FONT_7,
            Captcha.FONT_8,
            Captcha.FONT_9,
            Captcha.FONT_10};

    @Autowired
    private Random random;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void createCaptcha(HttpServletResponse response, String captchaKey)throws Exception {
        if (TextUtils.isEmpty(captchaKey) || captchaKey.length() < 13) {
            return;
        }
        long key;
        try {
            key = Long.parseLong(captchaKey);
        } catch (Exception e) {
            return;
        }
        log.info("key == > " + key);
        //可以用了
        //设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        int captchaType = random.nextInt(3);
        Captcha targetCaptcha;
        if (captchaType == 0){
            // 三个参数分别为宽、高、位数
            targetCaptcha = new SpecCaptcha(200,60,5);
        }else if (captchaType == 1){
            //gif类型
            targetCaptcha = new GifCaptcha(200,60);
        }else {
            //算术类型
            targetCaptcha = new ArithmeticCaptcha(200,60);
            targetCaptcha.setLen(2);
            targetCaptcha.text();
        }

        int index = random.nextInt(captcha_font_types.length);
        log.info("captcha font type index == >"+ index);
        targetCaptcha.setFont(captcha_font_types[index]);
        targetCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        String content = targetCaptcha.text().toLowerCase();
        log.info("captcha content == > "+content);
        //保存到redis里头
        //删除时机
        //1.自然过期，也就是10分钟后自己
        //2.验证码用完以后删除
        //3.用完的情况：看get的地方
        redisUtils.set(Constants.User.KEY_CAPTCHA_CONTENT + key,content,60*10);
        targetCaptcha.out(response.getOutputStream());
    }

    @Autowired
    private TaskService taskService;
    /**
     * 发送邮箱验证码
     * 使用场景：注册、找回密码、修改邮箱(会输入新的邮箱)
     * 注册(register)：如果已经注册过了，就提示说，该邮箱已经注册
     * 找回密码(forget)：如果没有注册过，提示该邮箱没有注册
     * 修改邮箱(update)(新的邮箱)：如果已经注册了，提示该邮箱已经注册
     * @param request
     * @param emailAddress
     * @return
     */
    @Override
    public ResponseResult sendEmail(String type, HttpServletRequest request, String emailAddress) {
        if (emailAddress == null) {
            return ResponseResult.FAILED("邮箱地址不可以为空");
        }
        //根据类型，查询邮箱是否存在
        if ("register".equals(type) || "update".equals(type)){
            SobUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail != null) {
                return ResponseResult.FAILED("该邮箱已经注册");
            }
        }else if ("forget".equals(type)){
            SobUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail == null) {
                return ResponseResult.FAILED("该邮箱未注册");
            }
        }
        //1.防止暴力发送,就是不断发地送:同一个邮箱,间隔要超过30秒发一次,同一个IP,1小时内最多只能发10次(如果是短信,你最多只能发5次)
        String remoteAddr = request.getRemoteAddr();
        log.info("sendEmail == > ip == >"+remoteAddr);
        if (remoteAddr != null){
            remoteAddr= remoteAddr.replace(":","_");
        }
        //拿出来,如果没有,那就过了
        log.info("Constants.User.KEY_EMAIL_SEND_IP + remoteAddr === > " + Constants.User.KEY_EMAIL_SEND_IP + remoteAddr);
        Integer ipSendTime = (Integer) redisUtils.get(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr);
        if (ipSendTime != null && ipSendTime > 10) {
            return ResponseResult.FAILED("您发送验证码也太频繁了吧!");
        }
        Object hasEmailSend = redisUtils.get(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress);
        if (hasEmailSend != null ) {
            return ResponseResult.FAILED("您发送验证码也太频繁了吧!");
        }
        //2.检查邮箱地址是否正确
        boolean isEmailFormatOk = TextUtils.isEmailAddressOk(emailAddress);
        if (!isEmailFormatOk) {
            return ResponseResult.FAILED("邮箱地址格式不正确");
        }
        //0~999999
        int code = random.nextInt(999999);
        if ((code < 100000)) {
            code += 100000;
        }
        log.info("sendEmail == > code == > " + code);
        //3.发送验证码,6位数:100000~999999
        try {
            taskService.sendEmailVerifyCode(String.valueOf(code), emailAddress);
        } catch (Exception e) {
            return ResponseResult.FAILED("验证码发送失败,请稍后重试.");
        }
        //4.做记录
        //发送记录,code
        //
        if (ipSendTime == null){
            ipSendTime = 0;
        }
        ipSendTime++;
        //1小时有效期
        redisUtils.set(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr, ipSendTime, 60*60);
        redisUtils.set(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress,"true",30);
        //保存code,10分钟有效
        redisUtils.set(Constants.User.KEY_EMAIL_CODE_CONTENT+ emailAddress, String.valueOf(code), 60*10);
        return ResponseResult.SUCCESS("验证码发送成功");
    }

    @Override
    public ResponseResult register(SobUser sobUser, String emailCode, String captchaCode, String captchaKey, HttpServletRequest request) {
        //第一步:检查当前用户名是否已经注册
        String userName = sobUser.getUserName();
        if (TextUtils.isEmpty(userName)) {
            return ResponseResult.FAILED("用户名不可以为空");
        }
        SobUser userFromDbByUserName = userDao.findOneByUserName(userName);
        if (userFromDbByUserName != null) {
            return ResponseResult.FAILED("该用户名已注册");
        }
        //第二步:检查邮箱格式是否正确
        String email = sobUser.getEmail();
        if (TextUtils.isEmpty(email)) {
            return ResponseResult.FAILED("邮箱地址不可以为空.");
        }
        if (!TextUtils.isEmailAddressOk(email)) {
            return ResponseResult.FAILED("邮箱地址格式不正确");
        }
        //第三步:检查该邮箱是否已经注册
        SobUser userByEmail = userDao.findOneByEmail(email);
        if (userByEmail != null) {
            return ResponseResult.FAILED("该邮箱地址已经注册");
        }
        //第四步:检查邮箱验证码是否正确
        String emailVerifyCode = (String) redisUtils.get(Constants.User.KEY_EMAIL_CODE_CONTENT+ email);
        if (TextUtils.isEmpty(emailVerifyCode)) {
            return ResponseResult.FAILED("邮箱验证码已过期");
        }
        if (!emailVerifyCode.equals(emailCode)){
            return ResponseResult.FAILED("邮箱验证码不正确");
        } else {
            //正确，干掉redis里的内容
            redisUtils.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        }
        //第五步:检查图灵验证码是否正确
        String captchaVerifyCode = (String) redisUtils.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        if (TextUtils.isEmpty(captchaVerifyCode)) {
            return ResponseResult.FAILED("人类验证码已过期");
        }
        if (!captchaVerifyCode.equals(captchaCode)){
            return ResponseResult.FAILED("人类验证码不正确");
        } else {
          redisUtils.del(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        }
        //达到可以注册的条件
        //第六步:对密码进行加密
        String password = sobUser.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不可以为空");
        }
        sobUser.setPassword(bCryptPasswordEncoder.encode(sobUser.getPassword()));
        //第七步:补全数据
        //包括:注册IP,登录IP,角色,头像,创建时间,更新时间
        String ipAddress = request.getRemoteAddr();
        sobUser.setRegIp(ipAddress);
        sobUser.setLoginIp(ipAddress);
        sobUser.setUpdateTime(new Date());
        sobUser.setCreateTime(new Date());
        sobUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        sobUser.setRoles(Constants.User.ROLE_NORMAL);
        sobUser.setState("1");
        sobUser.setId(idWorker.nextId() + "");
        //第八步:保存到数据库中
        userDao.save(sobUser);
        //第九步:返回结果
        return ResponseResult.GET(ResponseState.JOIN_IN_SUCCESS);
    }

    @Override
    public ResponseResult doLogin(String captcha,
                                  String captchaKey,
                                  SobUser sobUser,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        String captchaValue = (String) redisUtils.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        if (!captcha.equals(captchaValue)) {
            return ResponseResult.FAILED("人类验证码不正确");
        }
        //验证成功，删除redis里的验证码
        redisUtils.del(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        String userName = sobUser.getUserName();
        if (TextUtils.isEmpty(userName)) {
            return ResponseResult.FAILED("账号不可以为空");
        }

        String password = sobUser.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不可以为空");
        }
        SobUser userFromDb = userDao.findOneByUserName(userName);
        if (userFromDb == null) {
            userFromDb = userDao.findOneByEmail(userName);
        }

        if (userFromDb == null) {
            return ResponseResult.FAILED("用户名或密码不正确");
        }
        //用户存在
        //对比密码
        boolean matches = bCryptPasswordEncoder.matches(password, userFromDb.getPassword());
        if (!matches) {
            return ResponseResult.FAILED("用户或密码不正确");
        }
        //密码正确
        //判断用户状态,如果是非正常的状态,则返回结果
        if (!"1".equals(userFromDb.getState())) {
            return ResponseResult.ACCOUNT_DENY();
        }
        createToken(response, userFromDb);
        return ResponseResult.SUCCESS("登录成功");
    }


    /**
     *
     * @param response
     * @param userFromDb
     * @return  token_key
     */
    private String createToken(HttpServletResponse response, SobUser userFromDb) {
        int deleteResult = refreshTokenDao.deleteAllByUserId(userFromDb.getId());
        log.info("deleteResult of refresh token .." + deleteResult);
        //生成token
        Map<String, Object> claims = ClaimsUtils.sobUser2Claims(userFromDb);
        //token默认有效为2小时
        String token = JwtUtil.createToken(claims);
        //返回token的md5值，token会保存到redis里
        //前端访问的时候，携带token的MD5key，从redis中获取即可
        String tokenKey = DigestUtils.md5DigestAsHex(token.getBytes());
        //保存token到redis里，有效期为2个小时，key是tokenKey
        redisUtils.set(Constants.User.KEY_TOKEN + tokenKey, token, Constants.TimeValue.HOUR_2);
        //把tokenKey写到cookies里
        //这个要动态获取，可以从request里获取
        CookieUtils.setUpCookie(response, Constants.User.COOKIE_TOKEN_KEY, tokenKey);
        //生成refreshToken
        String refreshTokenValue = JwtUtil.createRefreshToken(userFromDb.getId(), 60 * 60 * 24 * 30);
        //保存到数据库里
        //refreshToken, tokenKey, 用户ID, 创建时间, 更新时间
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(idWorker.nextId() + "");
        refreshToken.setRefreshToken(refreshTokenValue);
        refreshToken.setUserId(userFromDb.getId());
        refreshToken.setTokenKey(tokenKey);
        refreshToken.setCreateTime(new Date());
        refreshToken.setUpdateTime(new Date());
        refreshTokenDao.save(refreshToken);
        return tokenKey;
    }

    /**
     * 本质,通过携带的token_key检查用户是否有登录,如果登录了,就返回用户信息
     *
     * @return
     */
    @Override
    public SobUser checkSobUser() {

        //拿到token_key
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKIE_TOKEN_KEY);
        log.info("checkSobUer tokenKey == >" + tokenKey);
        SobUser sobUser = parseByTokenKey(tokenKey);
        if (sobUser == null) {
            //说明解析出错了
            //1.去mysql查询refreshToken
            RefreshToken refreshToken = refreshTokenDao.findOneByTokenKey(tokenKey);
            //2.如果不存在，就是当前访问没有登陆，提示用户登录
            if (refreshToken == null) {
                log.info("refresh token is null...");
                return null;
            }
            //3.如果存在，就解析refreshToken
            try {
                JwtUtil.parseJWT(refreshToken.getRefreshToken());
                //5.如果refreshToken有效，创建新的token和新的refreshToken
                String userId = refreshToken.getUserId();
                SobUser userFromDb = userDao.findOneById(userId);
                //删掉refreshToken的记录

                String newTokenKey = createToken(getResponse(), userFromDb);
                //返回token
                log.info("created new token and refreshToken....");
                return parseByTokenKey(newTokenKey);
            } catch (Exception e1){
                log.info("refresh token is 过期了...");
                //4.如果refreshToken过期了，就当前访问没有登录，提示用户登录
                return null;
            }
        }
        return sobUser;
    }

    @Override
    public ResponseResult getUserInfo(String userId) {
        //从数据库里获取
        SobUser user = userDao.findOneById(userId);
        //判断结果
        if (user == null) {
            //如果不存在，就返回不存在
            return ResponseResult.FAILED("用户不存在");
        }
        //如果存在，就复制对象、清空密码、Email、登录ID、注册IP
        String userJson = gson.toJson(user);
        SobUser newSobUser = gson.fromJson(userJson, SobUser.class);
        newSobUser.setPassword("");
        newSobUser.setEmail("");
        newSobUser.setRegIp("");
        newSobUser.setLoginIp("");
        //返回结果
        return ResponseResult.SUCCESS("获取成功").setData(newSobUser);
    }

    @Override
    public ResponseResult checkEmail(String email) {
        SobUser user = userDao.findOneByEmail(email);
        return user == null ? ResponseResult.FAILED("该邮箱未注册.") : ResponseResult.SUCCESS("该邮箱已被注册.");
    }

    @Override
    public ResponseResult checkUserName(String userName) {
        SobUser user = userDao.findOneByUserName(userName);
        return user == null ? ResponseResult.FAILED("该用户名未注册.") : ResponseResult.SUCCESS("该用户名已被注册.");
    }

    /**
     * 更新用户信息
     *
     * @param userId
     * @param sobUser
     * @return
     */
    @Override
    public ResponseResult updateUserInfo( String userId, SobUser sobUser) {
        //从token里解析出来的user，为了校验权限
        //只有用户才可以修改自己的信息
        SobUser userFromTokenKey = checkSobUser();
        if (userFromTokenKey == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        SobUser userFromDb = userDao.findOneById(userFromTokenKey.getId());
        //判断用户的ID和即将要修改的用户ID是否一致，如果一致才可以修改
        if (!userFromDb.getId().equals(userId)) {
            return ResponseResult.PERMISSION_DENY();
        }
        //可以进行修改
        //可经修改的项
        //用户名
        String userName = sobUser.getUserName();
        if (!TextUtils.isEmpty(userName)) {
            SobUser userByUserName = userDao.findOneByUserName(userName);
            if (userByUserName != null) {
                return ResponseResult.FAILED("该用户名已注册");
            }
            userFromDb.setUserName(userName);
        }
        //头像
        if (!TextUtils.isEmpty(sobUser.getAvatar())) {
            userFromDb.setAvatar(sobUser.getAvatar());
        }
        //签名,可以为空
        userFromDb.setSign(sobUser.getSign());
        userDao.save(userFromDb);
        //干掉redis里的token，下一次请求，需要解析token的,就会根据refreshToken重新创建一个。
        //拿到request和response
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKIE_TOKEN_KEY);
        redisUtils.del(tokenKey);
        return ResponseResult.SUCCESS("用户信息更新成功");
    }

    private HttpServletRequest getRequest(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }
    private HttpServletResponse getResponse(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getResponse();
    }

    /**
     * 删除用户，并不是真的删除
     * 而是修改状态
     * <p>
     *     PS:需要管理员权限
     *
     *
     * @return
     */
    @Override
    public ResponseResult deleteUserById(String userId) {
        //可以删除用户
        int result = userDao.deleteUserByState(userId);
        if (result > 0) {
            return ResponseResult.SUCCESS("删除成功");
        }
        return ResponseResult.FAILED("用户不存在");
    }

    /**
     * 需要管理员权限
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult listUsers(int page, int size) {
        //可以获取用户列表
        //分页查询
        if (page < Constants.Page.DEFAULT_PAGE){
            page = Constants.Page.DEFAULT_PAGE;
        }
        //size限制，每一页不得少于5个
        if (size < Constants.Page.MIN_SIZE){
            size = Constants.Page.MIN_SIZE;
        }

        //根据注册日期来排序
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<SobUser> all = userDao.listAllUserNoPassword(pageable);
        return ResponseResult.SUCCESS("获取用户列表成功").setData(all);
    }

    /**
     * 更新密码
     * @param verifyCode
     * @param sobUser
     * @return
     */
    @Override
    public ResponseResult updateUserPassword(String verifyCode, SobUser sobUser) {
        //检查邮箱是否有填写
        String email = sobUser.getEmail();
        if (TextUtils.isEmpty(email)) {
            return ResponseResult.FAILED("邮箱不可以为空");
        }
        //根据邮箱去redis里拿验证
        //进行对比
        String redisVerifyCode = (String) redisUtils.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (redisVerifyCode == null || !redisVerifyCode.equals(verifyCode)){
            return ResponseResult.FAILED("验证码错误");
        }
        redisUtils.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        //修改密码
        int result = userDao.updatePasswordByEmail(bCryptPasswordEncoder.encode(sobUser.getPassword()), email);
        return result > 0 ? ResponseResult.SUCCESS("密码修改成功") : ResponseResult.FAILED("密码修改失败");
    }

    /**
     * 更新邮箱
     * @param email
     * @param verifyCode
     * @return
     */
    @Override
    public ResponseResult updateEmail(String email, String verifyCode) {
        //1.确保用户已经登录了
        SobUser sobUser = this.checkSobUser();
        //没有登陆
        if (sobUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //2.对比验证码，确保新的邮箱地址是属于当前用户的
        String redisVerifyCode = (String) redisUtils.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (TextUtils.isEmpty(redisVerifyCode) || !redisVerifyCode.equals(verifyCode)) {
            return ResponseResult.FAILED("验证码错误");
        }
        //验证码正确，删除验证码
        redisUtils.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        //可以修改邮箱
        int result = userDao.updateEmailById(email, sobUser.getId());

        return result > 0 ? ResponseResult.SUCCESS("邮箱修改成功") : ResponseResult.FAILED("邮箱修改失败");
    }

    @Override
    public ResponseResult doLogOut() {
        //拿到token_key
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKIE_TOKEN_KEY);
        if (TextUtils.isEmpty(tokenKey)) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //删除redis里的token
        redisUtils.del(Constants.User.KEY_TOKEN + tokenKey);
        //删除mysql里的refreshToken
        refreshTokenDao.deleteAllByTokenKey(tokenKey);
        //删除cookie里的token_key
        CookieUtils.deleteCookie(getResponse(), Constants.User.COOKIE_TOKEN_KEY);
        return ResponseResult.SUCCESS("退出登录成功");
    }

    private SobUser parseByTokenKey(String tokenKey){
        String token = (String) redisUtils.get(Constants.User.KEY_TOKEN + tokenKey);
        log.info("parseByTokenKey token == > " + token);
        if (token != null) {
            try {
                Claims claims = JwtUtil.parseJWT(token);
                return ClaimsUtils.claims2SobUser(claims);
            } catch (Exception e){
                log.info("parseByTokenKey == > " + tokenKey + "过期了...");
                return null;
            }
        }
        return null;
    }
}
