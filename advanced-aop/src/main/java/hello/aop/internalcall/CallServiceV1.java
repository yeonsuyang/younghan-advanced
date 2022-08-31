package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 참고: 생성자 주입은 순환 사이클을 만들기 때문에 실패한다. (자기자신을 주입받기때문에)
 */
@Slf4j
@Component
public class CallServiceV1 {

    private CallServiceV1 callServiceV1; //주입 받은 대상은 실제 자신이 아니라 프록시 객체이다.

    @Autowired
    public void setCallServiceV1(CallServiceV1 callServiceV1) { //생성자가 아닌 setter //이건 생성다끝나고 set하는거여서
        this.callServiceV1 = callServiceV1;
    }
    public void external() {
        log.info("call external");
        callServiceV1.internal(); //외부 메서드 호출 //프록시를 통한 호출
    }
    public void internal() {
        log.info("call internal");
    }
}