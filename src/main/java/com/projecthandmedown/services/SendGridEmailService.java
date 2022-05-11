package com.projecthandmedown.services;


import java.io.IOException;

import com.projecthandmedown.models.Message;
import com.projecthandmedown.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.*;


@Service
public class SendGridEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);
    @Value("${sendgrid.api.key}")
    private String sendgridKey;
    @Value("${sendgrid.api.email}")
    private String senderEmail;

    public String sendTextEmail() throws IOException {
        // the sender email should be the same as we used to Create a Single Sender Verification
        Email from = new Email(senderEmail);
        String subject = "The subject";
        Email to = new Email("testemail0333@yahoo.com");
        Content content = new Content("text/plain", "This is a test email");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendgridKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info(response.getBody());
            return response.getBody();
        } catch (IOException ex) {
            throw ex;
        }
    }

    public String sendTextEmail(Message message) throws IOException {
        // the sender email should be the same as we used to Create a Single Sender Verification
        Email from = new Email(senderEmail);
        String subject = "New Message From: " + message.getSender().getUsername() + " With a subject of: " + message.getSubject();
        Email to = new Email(message.getReceiver());
        System.out.println(message.getSender().getId());
        Content content = new Content("text/plain", "Message Content: \n" + message.getBody() + "\n To reply back use the following link: \n http://localhost:8080/messaging/7/" + message.getSender().getId());
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendgridKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info(response.getBody());
            return response.getBody();
        } catch (IOException ex) {
            throw ex;
        }
    }

    public String sendTextEmail(User user, String token) throws IOException {
        // the sender email should be the same as we used to Create a Single Sender Verification
        Email from = new Email(senderEmail);
        String subject = "Reset Password Link";
        Email to = new Email(user.getEmail());
        Content content = new Content("text/plain", "Here is the link to reset your password: \n"
        + "http://localhost:8080/reset_password?token=" + token);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendgridKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info(response.getBody());
            return response.getBody();
        } catch (IOException ex) {
            throw ex;
        }
    }
}
