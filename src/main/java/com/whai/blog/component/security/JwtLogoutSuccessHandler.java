package com.whai.blog.component.security;

import com.alibaba.druid.support.json.JSONUtils;
import com.whai.blog.utils.AjaxResult;
import com.whai.blog.utils.authenticate.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 登出处理器
 * 在用户退出登录时，
 *          我们需将原来的JWT置为空返给前端，这样前端会将空字符串覆盖之前的jwt，
 *          JWT是无状态化的，销毁JWT是做不到的，JWT生成之后，只有等JWT过期之后，才会失效。
 *          因此我们采取置空策略来清除浏览器中保存的JWT。
 *
 *          同时我们还要将我们之前置入SecurityContext中的用户信息进行清除，这可以通过创建SecurityContextLogoutHandler对象，调用它的logout方法完成
 *
 * 定义LogoutSuccessHandler接口的实现类JWTLogoutSuccessHandler，重写其onLogoutSuccess方法
 */
@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {



    @Autowired
    JwtUtils jwtUtils;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, authentication);
        }

        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();

        httpServletResponse.setHeader(jwtUtils.getHeader(), "");


        AjaxResult result = AjaxResult.success("SuccessLogout");
        outputStream.write(JSONUtils.toJSONString(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
