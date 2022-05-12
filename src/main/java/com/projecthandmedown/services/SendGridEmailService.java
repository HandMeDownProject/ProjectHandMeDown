package com.projecthandmedown.services;


import java.io.IOException;

import com.projecthandmedown.models.*;
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
        Email from = new Email(senderEmail);
        String subject = "New Message From: " + message.getSender().getUsername() + " With a subject of: " + message.getSubject();
        Email to = new Email(message.getReceiver());
        Content content = new Content("text/plain", "Message Content: \n" + message.getBody() + "\n To reply back use the following link: \n http://localhost:8080/messaging/7/" + message.getSender().getId() + "\nIf user has sent inappropriate content use the following link to report them.\n" + "http://localhost:8080/report/user/" + message.getSender().getId());
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

    public String sendTextEmail(User user) throws IOException {
        Email from = new Email(senderEmail);
        String subject = "Forgot Username.";
        Email to = new Email(user.getEmail());
        Content content = new Content("text/plain", "Here is the username used for this email. \n"
            + "Username: " + user.getUsername()  + "\nHere is the log in link.\n" + "http://localhost:8080/login");
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

    public String sendReportEmail(Message message, Activity activity) throws IOException {
        Email from = new Email(senderEmail);
        String subject = "New Report For Activity From: " + message.getSender().getUsername() + " Title: " + message.getSubject();
        Email to = new Email(senderEmail);
        Content content = new Content("text/plain", "Report Content: \n" + message.getBody() + "\nContents of reported activity: \n" + activity.getTitle() + "\n" + activity.getBody() + "\nPosted By: " + activity.getUser().getUsername() + "\nWith a Activity Id of " + activity.getId() + "\nLink to specific activity.\n" + "http://localhost:8080/activities/" + activity.getId());
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

    public String sendReportEmail(Message message, Listing listing) throws IOException {
        Email from = new Email(senderEmail);
        String subject = "New Listing Report From: " + message.getSender().getUsername() + " Title: " + message.getSubject();
        Email to = new Email(senderEmail);
        Content content = new Content("text/plain", "Report Content: \n" + message.getBody() + "\nContents of reported listing: \n" + listing.getTitle() + "\n" + listing.getBody() + "\nPosted By: " + listing.getUser().getUsername() + "\nWith a Listing Id of " + listing.getId() + "\nLink to specific activity.\n" + "http://localhost:8080/listing/" + listing.getId());
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

    public String sendReportEmail(Message message, ForumPost forumPost) throws IOException {
        Email from = new Email(senderEmail);
        String subject = "New Report For Forum Post From: " + message.getSender().getUsername() + " Title: " + message.getSubject();
        Email to = new Email(senderEmail);
        Content content = new Content("text/plain", "Report Content: \n" + message.getBody() + "\nContents of reported forum post: \n" + forumPost.getTitle() + "\n" + forumPost.getBody() + "\nPosted By: " + forumPost.getUser().getUsername() + "\nWith a Forum Post Id of " + forumPost.getId() + "\nLink to specific forum post.\n" + "http://localhost:8080/forum_post/" + forumPost.getId());
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

    public String sendReportEmail(Message message, ForumReply forumReply) throws IOException {
        Email from = new Email(senderEmail);
        String subject = "New Report For Forum Reply From: " + message.getSender().getUsername() + " Title: " + message.getSubject();
        Email to = new Email(senderEmail);
        Content content = new Content("text/plain", "Report Content: \n" + message.getBody() + "\nContents of reported reply: \n" + forumReply.getBody() + "\nPosted By: " + forumReply.getUser().getUsername() + "\nWith a Reply Id of " + forumReply.getId() + "\nLink to specific forum post reply is in.\n" + "http://localhost:8080/forum_post/" + forumReply.getForumPost().getId());
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

    public String sendReportEmail(Message message, User user) throws IOException {
        Email from = new Email(senderEmail);
        String subject = "New Report For User From: " + message.getSender().getUsername() + " Title: " + message.getSubject();
        Email to = new Email(senderEmail);
        Content content = new Content("text/plain", "Report Content: \n" + message.getBody() + "\nInformation of reported User: \n" + user.getUsername() + "\nEmail: " + user.getEmail() + "\nWith a User Id of " + user.getId() + "\nLink to specific User.\n" + "Link to added");
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
