package hello.proxy.pureproxy.proxy.code;

import hello.proxy.pureproxy.proxy.Subject;

public class ProxyPatternClient {
    private Subject subject;
    public ProxyPatternClient(Subject subject) {
        this.subject = subject;
    }
    public void execute() {
        subject.operation();
    }
}
