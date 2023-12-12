package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired
    CallService callService;


    @Test
    public void printProxy() throws Exception {
        log.info("callService class={}" , callService.getClass());
    }
    
    @Test
    public void internalCall() throws Exception {
        callService.internal(); //@Transactional 이 걸려있는 inertnal 메서드를 바로 실행시키면 제대로 트랜잭션이 걸려있음
    }

    @Test
    public void externalCall() throws Exception {
        //@Transactional 이 걸려 있지 않은 external 메서드에서
        //@Transactional 이 걸려 있는 internal 메서드를 실행 시키면 트랜잭션이 활성화 되어 있지 않음
        /*
        1. 클라이언트인 테스트 코드는 callService.external() 을 호출한다. 여기서 callService 는 트랜잭션 프록시이다.
        2. callService 의 트랜잭션 프록시가 호출된다.
        3. external() 메서드에는 @Transactional 이 없다. 따라서 트랜잭션 프록시는 트랜잭션을 적용하지 않는다.
        4. 트랜잭션 적용하지 않고, 실제 callService 객체 인스턴스의 external() 을 호출한다.
        5. external() 은 내부에서 internal() 메서드를 호출한다. 그런데 여기서 문제가 발생한다.
         */
        callService.external();
    }


    @TestConfiguration
    static class InternalCallV1Config{
        @Bean
        CallService callService(){
            return new CallService();
        }
    }

    @Slf4j
    static class CallService{
        //@Transactional
        public void external(){
            log.info("---- call external ----");
            printTxInfo();
            //내부 호출은 프록시를 거치지 않음
            //@Transactional를 사용하는 트랜잭션은 AOP프록시를 사용한다. 프록시를 사용하면 메서드 내부 호출에 프록시를 적용할 수 없음.
            internal();


        }

        @Transactional
        public void internal(){
            log.info("=== call internal ===");
            printTxInfo();
        }

        private void printTxInfo(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
            System.out.println("currentTransactionName = " + currentTransactionName);

            Integer currentTransactionIsolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
            System.out.println("currentTransactionIsolationLevel = " + currentTransactionIsolationLevel);

            log.info("tx active = {}" , txActive);
        }
    }
}
