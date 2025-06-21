package com.orion.authenticationservice.conf;


import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.orion.authenticationservice.common.Result;
import com.orion.authenticationservice.constant.user.UserErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/28
 * @Description:
 */
@Component
@Slf4j
public class JwtHandler implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            refuseResult(response);
            return false;
        }
        return true;
    }

    private void refuseResult(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Result<Object> result = new Result<>().setCode(40101)
                .setMessage("签名验证失败!");
        response.getWriter().println(JSONUtil.toJsonStr(result));
        response.getWriter().flush();

    }
}
