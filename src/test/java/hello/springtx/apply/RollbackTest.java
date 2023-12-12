package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RollbackTest {

    @Autowired RollbackService service;

    @Test
    public void runtimeException() throws Exception {
        Assertions.assertThatThrownBy(
                () -> service.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void checkedException() throws Exception {
        Assertions.assertThatThrownBy(
                        () -> service.checkedException())
                .isInstanceOf(RollbackService.MyException.class);
    }

    @Test
    public void rollbackFor() throws Exception {
        Assertions.assertThatThrownBy(
                        () -> service.checkedException())
                .isInstanceOf(RollbackService.MyException.class);
    }

    @TestConfiguration
    static class RollbackTestConfig{

        @Bean
        RollbackService rollbackService(){
            return new RollbackService();
        }
    }


    @Slf4j
    static class RollbackService{
        //런타임 예외 발생 : 롤백
        @Transactional
        public void runtimeException(){
            log.info("call runtimeException");
            throw new RuntimeException();
        }
        //체크 예외 발생 : 커밋
        @Transactional
        public void checkedException() throws MyException{
            log.info("call checkedException");
            throw new MyException();
        }

        //MyException
        static class MyException extends Exception{

        }

        //체크 예외 rollbackFor 지정 : 롤백
        //rollbackFor 옵션을 사용
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException{
            log.info("call checkedException");
            throw new MyException();
        }
    }
}
