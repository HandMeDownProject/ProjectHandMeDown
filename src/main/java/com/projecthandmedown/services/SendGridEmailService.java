package com.projecthandmedown.services;


import java.io.IOException;

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

}

