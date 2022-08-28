package hello.proxy.config.v6_app.aspect;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LogTraceAspect {
    private final LogTrace logTrace;
    public LogTraceAspect(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    @Around("execution(* hello.proxy.app..*(..))") //포인트컷 어드바이저 다 지정하는 것
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        // log.info("target={}", joinPoint.getTarget()); //실제 호출 대상
        // log.info("getArgs={}", joinPoint.getArgs()); //전달인자
        // log.info("getSignature={}", joinPoint.getSignature()); //join point 시그니처
        try {
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);
            //로직 호출
            Object result = joinPoint.proceed();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}