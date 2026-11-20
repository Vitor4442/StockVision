package stockvision.procurement.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "stockvision.exchange";
    public static final String PROCUREMENTS_APPROVE = "procurements.Approve.queue";
    public static final String PROCUREMENTS_APPROVE_ROUTING_KEY = "procurements.Approve";

    @Bean
    public TopicExchange procurementExchange() {return new TopicExchange(EXCHANGE);}

    @Bean
    public Queue procurementApproveQueue(){ return new Queue(PROCUREMENTS_APPROVE);}

    @Bean
    public Binding stockLowBinding() {
        return BindingBuilder
                .bind(procurementApproveQueue())
                .to(procurementExchange())
                .with(PROCUREMENTS_APPROVE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {return new JacksonJsonMessageConverter();}

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }



}
