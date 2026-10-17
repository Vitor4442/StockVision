package stockvision.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import stockvision.notification.NotificationService;
import stockvision.notification.config.RabbitMQConfig;
import stockvision.notification.event.StockLowEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;


    @RabbitListener(queues = RabbitMQConfig.STOCK_LOW_QUEUE)
    public void consume(StockLowEvent event) {
        notificationService.processStockLow(event);
        log.warn(
                "Estoque baixo recebido: produto={}, local={}, quantidade atual={}, quantidade mínima={}",
                event.productId(),
                event.locationId(),
                event.currentQuantity(),
                event.minimumQuantity()
        );
    }
}
