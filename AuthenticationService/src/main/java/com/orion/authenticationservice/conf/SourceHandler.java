package com.orion.authenticationservice.conf;


import cn.hutool.json.JSONUtil;
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
public class SourceHandler implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader("X-Request-Source");
        if (!"InfiniteChat-GateWay".equals(header)) {
            refuseResult(response);
            return false;
        }
        return true;
    }

    private void refuseResult(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        Result<Object> result = new Result<>().setCode(UserErrorEnum.ILLEGAL_REQUEST_ERR.getCode())
                .setMessage(UserErrorEnum.ILLEGAL_REQUEST_ERR.getMessage());
        response.getWriter().println(JSONUtil.toJsonStr(result));
        response.getWriter().flush();

    }
}
