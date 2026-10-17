package stockvision.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import stockvision.notification.client.UsersNotificationClient;
import stockvision.notification.event.StockLowEvent;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String senderAddress;
    private final UsersNotificationClient usersNotificationClient;
    private final String internalToken;

    public EmailService(JavaMailSender mailSender, UsersNotificationClient usersNotificationClient, @Value("${app.mail.from}") String senderAddress, @Value("${app.internal.token:}") String internalToken) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
        this.usersNotificationClient = usersNotificationClient;
        this.internalToken = internalToken;
    }

    public void sendStockLowEmail(StockLowEvent event) {
        String[] recipients = findRecipients();
        if (recipients == null || java.util.Arrays.stream(recipients).noneMatch(StringUtils::hasText)) {
            throw new IllegalStateException("Nenhum destinatário disponível para o alerta de estoque baixo");
        }

        for (String recipient : recipients) {
            sendStockLowEmail(event, recipient);
        }
    }

    private String[] findRecipients() {
        return usersNotificationClient.findRecipients(internalToken);
    }

    private void sendStockLowEmail(StockLowEvent event, String recipient) {
        if (!StringUtils.hasText(recipient)) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderAddress);
        message.setTo(recipient);
        message.setSubject("Alerta de estoque baixo");
        message.setText("""
                O estoque atingiu o limite mínimo.

                Produto: %d
                Local: %d
                Quantidade atual: %s
                Quantidade mínima: %s
                """.formatted(
                event.productId(),
                event.locationId(),
                event.currentQuantity().toPlainString(),
                event.minimumQuantity().toPlainString()
        ));

        mailSender.send(message);
    }
}
