package stockvision.inventory.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "stockvision.exchange";
    public static final String STOCK_LOW_QUEUE = "stock.low.queue";
    public static final String STOCK_LOW_ROUTING_KEY = "stock.low";

    @Bean
    public TopicExchange stockExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue stockLowQueue() {
        return new Queue(STOCK_LOW_QUEUE, true);
    }

    @Bean
    public Binding stockLowBinding() {
        return BindingBuilder
                .bind(stockLowQueue())
                .to(stockExchange())
                .with(STOCK_LOW_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}