package com.lonk.busmodel.config.impl;

import com.lonk.busmodel.config.IUserIdConfigSupplier;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.Optional;

/**
 * 默认用户标识提供器
 *
 * @author lonk
 */
public class DefaultUserIdConfigSupplier implements IUserIdConfigSupplier {
    /**
     * 用户id请求头名
     */
    private final static String USER_ID_HEADER = "userId";

    @Override
    public String getUserId(ProceedingJoinPoint pjp) {
        return Optional.ofNullable(getRequest())
                .map(req -> req.getHeader(USER_ID_HEADER))
                .orElse(StringUtils.EMPTY);
    }

    private static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            return null;
        }

        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

}
