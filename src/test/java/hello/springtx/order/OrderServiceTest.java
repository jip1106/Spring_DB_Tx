package hello.springtx.order;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void order() throws Exception {
        //given
        Order order = new Order();
        order.setUsername("정상");

        //when
        orderService.order(order);

        //then
        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("완료");

    }

    //RuntimeException -> 롤백
    @Test
    public void runtimeException() throws Exception {
        //given
        Order order = new Order();
        order.setUsername("예외");

        //when
        assertThatThrownBy(() -> orderService.order(order))
                        .isInstanceOf(RuntimeException.class);

        //then
        Optional<Order> orderOptional = orderRepository.findById(order.getId());

        assertThat(orderOptional.isEmpty()).isTrue();
    }

    @Test
    public void bizException() throws Exception {
        //given
        Order order = new Order();
        order.setUsername("잔고 부족");

        //when
        try{
            orderService.order(order);
        }catch(NotEnoughMoneyException e){
            log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
        }



        //then
        Optional<Order> orderOptional = orderRepository.findById(order.getId());
        Order findOrder = orderOptional.get();

        assertThat(findOrder.getPayStatus()).isEqualTo("대기");
    }
}