package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ExecutionTest {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    @Test
    void printMethod() {
    //public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        log.info("helloMethod={}", helloMethod);
    }

    /*
    - 접근제어자?: public
    - 반환타입: String
    - 선언타입?: hello.aop.member.MemberServiceImpl
    - 메서드이름: hello
    - 파라미터: (String)
    - 예외?: 생략
     */
    @Test
    void exactMatch() {
        //public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }

    /*
        접근제어자?: 생략
        반환타입: *
        선언타입?: 생략
        메서드이름: *
        파라미터: (..) //파라미터 수가 상관없다는 뜻
        예외?: 없음
     */
    @Test
    void allMatch() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }


    @Test
    void nameMatch() {
        pointcut.setExpression("execution(* hello(..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }
    @Test
    void nameMatchStar1() {
        pointcut.setExpression("execution(* hel*(..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }
    @Test
    void nameMatchStar2() {
        pointcut.setExpression("execution(* *el*(..))"); //앞뒤로 다 됨
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }
    @Test
    void nameMatchFalse() {
        pointcut.setExpression("execution(* nono(..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isFalse();
    }


    /*
    hello.aop.member.*(1).*(2)
    (1): 타입
    (2): 메서드 이름
    패키지에서 . , .. 의 차이를 이해해야 한다.
    . : 정확하게 해당 위치의 패키지
    .. : 해당 위치의 패키지와 그 하위 패키지도 포함
     */
    @Test
    void packageExactMatch1() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactMatch2() {
        pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }
    @Test
    void packageExactMatchFalse() {
        pointcut.setExpression("execution(* hello.aop.*.*(..))"); //aop..* 으로하면 성공함
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isFalse();
    }

    @Test
    void packageMatchSubPackage2() {
        pointcut.setExpression("execution(* hello.aop..*.*(..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageMatchSubPackage1() {
        pointcut.setExpression("execution(* hello.aop.member..*.*(..))"); // ..: 서브패키지 다 포함
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }

    /*
    타입 매칭 - 부모 타입 허용
     */
    @Test
    void typeExactMatch() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }
    @Test
    void typeMatchSuperType() {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue(); //부모타입으로 해도 성공한다. 부모타입 = 자식타입
    }

    /*
    타입 매칭 - 부모 타입에 있는 메서드만 허용
     */
    @Test
    void typeMatchInternal() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal",
                String.class); //memberServiceImpl에만 있는 것
        assertThat(pointcut.matches(internalMethod,
                MemberServiceImpl.class)).isTrue();
    }
    //포인트컷으로 지정한 MemberService 는 internal 이라는 이름의 메서드가 없다.
    @Test
    void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal",
                String.class);
        assertThat(pointcut.matches(internalMethod,
                MemberServiceImpl.class)).isFalse(); //부모에 선언되어있는 메서드까지만 됨
    }

/*
execution 파라미터 매칭 규칙은 다음과 같다.
(String) : 정확하게 String 타입 파라미터
() : 파라미터가 없어야 한다.
(*) : 정확히 하나의 파라미터, 단 모든 타입을 허용한다.
(*, *) : 정확히 두 개의 파라미터, 단 모든 타입을 허용한다.
(..) : 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다. 참고로 파라미터가 없어도 된다. 0..* 로
이해하면 된다.
(String, ..) : String 타입으로 시작해야 한다. 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다.
예) (String) , (String, Xxx) , (String, Xxx, Xxx) 허용
 */
    //String 타입의 파라미터 허용
    //(String)
    @Test
    void argsMatch() {
        pointcut.setExpression("execution(* *(String))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }
    //파라미터가 없어야 함
    //()
    @Test
    void argsMatchNoArgs() {
        pointcut.setExpression("execution(* *())");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isFalse();
    }

    //정확히 하나의 파라미터 허용, 모든 타입 허용
//(Xxx)
    @Test
    void argsMatchStar() {
        pointcut.setExpression("execution(* *(*))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }
    //숫자와 무관하게 모든 파라미터, 모든 타입 허용
        //파라미터가 없어도 됨
        //(), (Xxx), (Xxx, Xxx)
    @Test
    void argsMatchAll() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }

    //String 타입으로 시작, 숫자와 무관하게 모든 파라미터, 모든 타입 허용
    //(String), (String, Xxx), (String, Xxx, Xxx) 허용
    @Test
    void argsMatchComplex() {
        pointcut.setExpression("execution(* *(String, ..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isTrue();
    }
}