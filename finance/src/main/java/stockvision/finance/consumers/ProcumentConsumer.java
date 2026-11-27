package stockvision.finance.consumers;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import stockvision.finance.Financial.FinancialService;
import stockvision.finance.Financial.events.ProcumentCreate;

@Component
@RequiredArgsConstructor
public class ProcumentConsumer {

    private final FinancialService financialService;

    @RabbitListener(queues = "procurements.Approve.queue")
    public void consumer(ProcumentCreate message) {
        financialService.createByProcumentEvent(message);
    }
}
