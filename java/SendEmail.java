package com.drew.github;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendEmail {
	
	final static String username = "GMAIL_USERNAME";
	final static String password = "GMAIL_PASSWORD";

	public static void gmail() throws UnsupportedEncodingException{
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

        String msgBody = "Hey Dude,\n\n Your Divvy-to-MapQuest mileage calculator has finished computing!\n\nLove,\n Your MacBook";

        try {
        	 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("EMAIL_FROM_ADDRESS"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("EMAIL_TO_ADDRESS"));
			message.setSubject("Divvy trip MapQuest processing is DONE-ZO!");
			message.setText(msgBody);
 
			Transport.send(message);
 
			System.out.println("Done");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
		
	}

}
