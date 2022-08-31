package hello.aop.internalcall;

import hello.aop.internalcall.aop.CallLogAspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@Import(CallLogAspect.class)
@SpringBootTest
class CallServiceV0Test {
    @Autowired
    CallServiceV0 callServiceV0; //프록시가 올라감

    @Test
    void external() {
      //  callServiceV0.getClass(); //프록시 호출됨
        callServiceV0.external();
    }
    @Test
    void internal() {
        callServiceV0.internal();
    }
}