package stockvision.inventory.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import stockvision.inventory.config.RabbitMQConfig;

@Component
public class StockEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public StockEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishStockLow(StockLowEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.STOCK_LOW_ROUTING_KEY,
                event
        );
    }
}