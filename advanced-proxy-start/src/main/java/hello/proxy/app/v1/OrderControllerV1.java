package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping
//스프링은 @Controller 또는 @RequestMapping 이 있어야 스프링 컨트롤러로 인식해야, HTTP URL이 매핑되고 동작한다.
//인터페이스에도 해당 어노테이션들 사용해도 됨
@ResponseBody
public interface OrderControllerV1 { //interface로 생성

    @GetMapping("/v1/request")
    String request(@RequestParam("itemId") String itemId);
    //인터페이스는 requestparam 안넣으면 안먹힐때가있음

    @GetMapping("/v1/no-log")
    String noLog();

}