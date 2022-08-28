package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class AspectV1 {

    //hello.aop.order 패키지와 하위 패키지
    @Around("execution(* hello.aop.order..*(..))") //포인트컷
    //어드바이스 : doLog
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature()); //join point 시그니처 //메서드의 전부
        return joinPoint.proceed(); //target 호출
    }
}
