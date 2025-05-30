package com.example.demo;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProcessor {
    private final JmsTemplate jmsTemplate;
    private final CommandService commandService;

    public MessageProcessor(JmsTemplate jmsTemplate, CommandService commandService) {
        this.jmsTemplate = jmsTemplate;
        this.commandService = commandService;
    }

    @JmsListener(destination = "INQ")
    public void processMessage(String message) {
        String[] commandParts = message.split(" ");
        String command = commandParts[0];

        String response;
        switch (command) {
            case "BUY":
                if (commandParts.length < 3) {
                    response = "Invalid BUY command";
                } else {
                    String security = commandParts[1];
                    int amount = Integer.parseInt(commandParts[2]);
                    response = commandService.buySecurity(security, amount);
                }
                break;
            case "ADD":
                if (commandParts.length < 2) {
                    response = "Invalid ADD command";
                } else {
                    String security = commandParts[1];
                    response = commandService.addSecurity(security);
                }
                break;
            case "SELL":
                if (commandParts.length < 3) {
                    response = "Invalid SELL command";
                } else {
                    String security = commandParts[1];
                    int amount = Integer.parseInt(commandParts[2]);
                    response = commandService.sellSecurity(security, amount);
                }
                break;
            case "PORTFOLIO":
                response = commandService.getSecurities();
                break;
            default:
                response = "Unknown command";
        }

        jmsTemplate.convertAndSend("OUTQ", response);
    }
}

