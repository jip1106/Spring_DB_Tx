package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
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
//메서드 내부 호출 때문에 트랜잭션 프록시가 적용되지 않는 문제를 해결하기 위해 internal 메서드를 별도의 클래스로 분리
public class InternalCallV2Test {

    @Autowired
    CallService callService;


    @Test
    public void printProxy() throws Exception {
        log.info("callService class={}" , callService.getClass());
    }
  

    @Test
    public void externalCallV2() throws Exception {
        /*
             메서드 내부호출을 클래스를 분리하여 호출
         */
        callService.external();
    }


    @TestConfiguration
    static class InternalCallV1Config{
        @Bean
        CallService callService(){
            return new CallService(internalService());
        }

        @Bean
        InternalService internalService(){
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService{

        private final InternalService internalService;

        public void external(){
            log.info("---- call external ----");
            printTxInfo();
            //내부 호출은 프록시를 거치지 않음
            //@Transactional를 사용하는 트랜잭션은 AOP프록시를 사용한다. 프록시를 사용하면 메서드 내부 호출에 프록시를 적용할 수 없다.
            internalService.internal();
        }
/*
        //내부 호출을 외부 호출로 변경(별도의 클래스로 변경)
        @Transactional
        public void internal(){
            log.info("=== call internal ===");
            printTxInfo();
        }
*/
        private void printTxInfo(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
            System.out.println("currentTransactionName = " + currentTransactionName);
            
            log.info("tx active = {}" , txActive);
        }
    }

    static class InternalService{
        @Transactional
        public void internal(){
            log.info("=== call internal ===");
            printTxInfo();
        }

        private void printTxInfo(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
            System.out.println("currentTransactionName = " + currentTransactionName);

            log.info("tx active = {}" , txActive);
        }
    }
}
