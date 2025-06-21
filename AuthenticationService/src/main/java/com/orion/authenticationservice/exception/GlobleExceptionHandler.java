package com.orion.authenticationservice.exception;


import com.orion.authenticationservice.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */

@Slf4j
@RestControllerAdvice
public class GlobleExceptionHandler {
    @ExceptionHandler(value = Throwable.class)
    public Result<?> handlerException(Throwable e) {
        log.error("未知错误:", e);

        return Result.serverError(e.getMessage());
    }

    @ExceptionHandler(value = UserException.class)
    public Result<?> handlerUserException(UserException e) {
        log.error("用户信息错误:{}", e);

        return Result.userError(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = DataBaseException.class)
    public Result<?> handlerUserException(DataBaseException e) {
        log.error("数据库错误:{}", e);

        return Result.databaseError(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<?> handlerValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        log.error("数据校验出现问题{}, 错误信息为:{}", e.getMessage(), errorMap);
        return Result.validError(errorMap.toString());
    }

}
