package com.kswist.statistics.utils;

import java.io.Serializable;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jboss.logging.Logger;

@Named("mailSender")
@Stateless
public class MailSender implements Serializable {

	private static final long serialVersionUID = -6316211111511366421L;

	private static final Logger logger = Logger.getLogger(MailSender.class);

	public void send(String from, String to, String subject, String msg) {

		try {
			Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", "umail.lucent.com");
			Session mailSession = Session.getDefaultInstance(properties);
			MimeMessage message = new MimeMessage(mailSession);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(to));
			message.setSubject(subject);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(msg, "text/html");
			Multipart multipart = new MimeMultipart("mixed");
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			logger.debug("Sending email: " + " from: " + from + " to:" + to
					+ " with subject: " + message.getSubject());
			Transport.send(message);
		} catch (Exception e) {
			logger.fatal("Sending email failed!");
			e.printStackTrace();
		}

	}

}
