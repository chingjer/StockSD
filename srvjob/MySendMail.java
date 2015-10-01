/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author huangtm
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class MySendMail {

    String host; //"smtp.gmail.com"
    int port; // 587

    MySendMail() {
        BufferedReader inf;
        String s1, aa[];
        try {
            inf = new BufferedReader(new FileReader(DbConfig.MAIL_PARA));
            while ((s1 = inf.readLine()) != null) {
                System.out.println(s1);
                aa = s1.split("=");
                if ("host".equals(aa[0].trim().toLowerCase())) {
                    host = aa[1].trim();
                }
                if ("port".equals(aa[0].trim().toLowerCase())) {
                    port = Integer.parseInt(aa[1].trim());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("MySendMail() FileNot Found!");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("MySendMail() IOException ");
            System.exit(-1);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

    }

    public static void main(String[] args) {
        String recipient;//收件人，如 xxx@gmail.com
        String subject; //主旨
        String content;
        MySendMail mail = new MySendMail();
        Properties props = new Properties();
        
        props.put("mail.smtp.host", mail.host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", mail.port);
        if (args.length < 3) {
            System.out.println("Usage: java MySendMail recipian, subject, content");
            System.exit(-1);
        }
        recipient = args[0];
        subject = args[1];
        content = args[2];
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(DbConfig.MAIL_USER, DbConfig.MAIL_PWD);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(DbConfig.MAIL_USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(content);

            Transport transport = session.getTransport("smtp");
            transport.connect(mail.host, mail.port, DbConfig.MAIL_USER, DbConfig.MAIL_PWD);

            Transport.send(message);
            System.out.println("寄送email結束.");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

}
