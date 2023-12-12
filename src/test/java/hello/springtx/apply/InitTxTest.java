package hello.springtx.apply;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

//트랜잭션 AOP 주의 사항 - 초기화 시점
//스프링 초기화 시점에는 트랜잭션 AOP가 적용되지 않을 수 있다.
@SpringBootTest
public class InitTxTest {
    @Autowired Hello hello;

    @Test
    public void go() throws Exception {
        //초기화 코드는 스프링이 초기화 시점에 호출한다
    }

    @TestConfiguration
    static class InitTxTestConfig {
        @Bean
        Hello hello() {
            return new Hello();
        }
    }

    @Slf4j
    static class Hello{

        @PostConstruct
        @Transactional
        public void initV1() {

            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active={}", isActive);

        }

        @EventListener(value = ApplicationReadyEvent.class)
        @Transactional
        public void init2() {

            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init ApplicationReadyEvent tx active={}", isActive);

        }
    }


}
