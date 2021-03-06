package com.poembook.poembook.core.utilities.service;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.poembook.poembook.core.utilities.service.EmailConstant.*;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

@Service
public class EmailService {
    @Value("${email.password}")
    private String PASSWORD;

    public void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
        Message message = createNewPasswordEmail(firstName, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public void sendNewForgetPasswordEmail(String firstName, String url, String email) throws MessagingException {
        Message message = createForgetPasswordEmail(firstName, url, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public void sendYouHaveMessageEmail(String firstName, String email, String senderName) throws MessagingException {
        Message message = createYouHaveMessageEmail(firstName, senderName, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }
    public void sendYourRoleChangedEmail(String firstName, String email, String role) throws MessagingException {
        Message message = createYourRoleChangedEmail(firstName, email, role);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private Message createNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT_NEW_PASSWORD);
        message.setText("Merhaba " + firstName + ", \n \n Yeni hesab??n??n ??ifresi burada:  " + password + "\n \n Poembook Y??netimi");
        message.setSentDate(new Date());
        message.saveChanges();

        return message;
    }

    private Message createForgetPasswordEmail(String firstName, String url, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject("??ifreni S??f??rla");
        message.setText("Merhaba " + firstName + ", \n \n Hesab??n??n ??ifresini s??f??rlamak istiyorsan t??kla:  " + url + "\n \n Poembook Y??netimi");
        message.setSentDate(new Date());
        message.saveChanges();

        return message;
    }

    private Message createYouHaveMessageEmail(String firstName, String senderName, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject("Mesaj??n Var");
        message.setText("Merhaba " + firstName + ", \n \n" + senderName + "sana mesaj g??nderdi  " + "\n \n Poembook Y??netimi");
        message.setSentDate(new Date());
        message.saveChanges();

        return message;
    }
    private Message createYourRoleChangedEmail(String firstName,  String email,String role) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject("Yetkilerin G??ncellendi");
        message.setText("Merhaba " + firstName + ", \n \n Poembook yetkilerin g??ncellendi \n \n Art??k yeni rol??n: " + role + "\n \n Poembook Y??netimi");
        message.setSentDate(new Date());
        message.saveChanges();

        return message;
    }

    private Session getEmailSession() {

        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);

        return Session.getInstance(properties, null);
    }
}
