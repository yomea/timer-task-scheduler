package com.shinemo.task.web.interceptor;

import static com.shinemo.Aace.RetCode.RET_SUCCESS;
import static com.shinemo.common.tools.exception.CommonErrorCodes.INVALID_TOKEN;

import com.shinemo.Aace.MutableBoolean;
import com.shinemo.Aace.MutableString;
import com.shinemo.Aace.imlogin.client.IMLoginClient;
import com.shinemo.Aace.userprofice.client.UserProficeClient;
import com.shinemo.common.tools.LoginContext;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.web.IgnoreAuth;
import com.shinemo.common.web.Requests;
import com.shinemo.conf.Config;
import java.lang.reflect.Method;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Slf4j
@Component
public class ApiInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private UserProficeClient userProficeClient;

    @Resource
    private IMLoginClient imLoginClient;

    /**
     * 在具体业务方法被执行前调用
     * 主要功能是初始化登录上下文
     * 验证token和权限
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        boolean handlerMethodBoolean = handler instanceof HandlerMethod;
        if (!handlerMethodBoolean) { //静态资源映射
            return true;
        }

        final Method methodApi = ((HandlerMethod) handler).getMethod();
        final IgnoreAuth ignoreAuth = methodApi.getAnnotation(IgnoreAuth.class);
        if (ignoreAuth != null) { //不需要登陆
            return true;
        }
        LoginContext.get();
        //8.Token校验
        if (!checkToken(request)) {
            throw new ApiException(INVALID_TOKEN);
        }
        //10.校验通过,controller中的方法(method)就会被执
        return true;
    }

    private boolean checkToken(final HttpServletRequest request) {
        if (Config.get("ENV").equals("daily") && request.getParameter("debug") != null) {
            LoginContext.setUid(request.getParameter("userId"));
            LoginContext.setOrgId(request.getParameter("orgId"));
            LoginContext.get().put("mobile", request.getParameter("mobile"));
            return true;
        }

        Cookie[] cookies = request.getCookies();
        String token = Requests.getValueFromCookies("token", cookies);
        String timestamp = Requests.getValueFromCookies("timeStamp", cookies);
        String uid = Requests.getValueFromCookies("userId", cookies);
        String orgId = Requests.getValueFromCookies("orgId", cookies);
        String mobile = Requests.getValueFromCookies("mobile", cookies);

        log.info("checkToken : token = " + token + ", timestamp = " + timestamp + ", uid = " + uid + ", orgId = " + orgId + ", mobile = " + mobile);
        if (StringUtils.isBlank(token) || StringUtils.isBlank(uid)) {
            log.error("login verify token fail,token or uid from cookies is not allow blank");
            return false;
        }
        int ret = -1;
        MutableBoolean isSuccess = new MutableBoolean();
        try {
            ret = imLoginClient.verifyToken(uid, token, NumberUtils.toLong(timestamp), isSuccess);
        } catch (Exception ex) {
            log.error("checkToken fail, token=" + token, ex);
        }

        if (ret != RET_SUCCESS || !isSuccess.get()) {
            log.error("login verify token fail,uid:{},token:{},timestamp:{},retCode:{}", uid, token, timestamp, ret);
            return false;
        }
        //校验是否是商机活动用户,是否是浙江移动用户
        if (StringUtils.isBlank(mobile)) {
            mobile = getMobile(uid);
        }
        LoginContext.setUid(uid);
        LoginContext.setOrgId(orgId);
        LoginContext.setUserName(getUserName(uid));
        LoginContext.get().put("mobile", mobile);
        return true;
    }


    private String getUserName(String uid) {
        MutableString name = new MutableString();
        int result = userProficeClient.getName(uid, name);
        if (result == 0) {
            return name.get();
        }
        return "";
    }


    private String getMobile(String uid) {
        MutableString mobile = new MutableString();
        int ret = userProficeClient.getMobile(uid, mobile);
        if (ret == 0) {
            return mobile.get();
        }
        return "";
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response,
                           final Object handler, final ModelAndView modelAndView) throws Exception {
    }


    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
                                final Object handler, final Exception ex) throws Exception {
        LoginContext.remove();
    }

    @Override
    public void afterConcurrentHandlingStarted(final HttpServletRequest request,
                                               final HttpServletResponse response, final Object handler) throws Exception {
        LoginContext.remove();
    }


}
