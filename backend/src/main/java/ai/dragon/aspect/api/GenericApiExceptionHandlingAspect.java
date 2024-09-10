package ai.dragon.aspect.api;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.dizitart.no2.exceptions.UniqueConstraintException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.dragon.dto.api.FailureApiResponse;
import jakarta.servlet.http.HttpServletResponse;

@Aspect
@Component
public class GenericApiExceptionHandlingAspect {
    @Autowired
    private HttpServletResponse response;

    @Around("@annotation(GenericApiExceptionHandling)")
    public Object handleException(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Exception ex) {
            int httpStatusCode = 500;
            if(ex instanceof UniqueConstraintException) {
                httpStatusCode = 422;
            }
            response.setStatus(httpStatusCode);

            return FailureApiResponse
                .builder()
                .msg(ex.getMessage())
                .code(String.format("0%d", httpStatusCode))
                .build();
        }
    }
}
