package com.kstrata.apps.tsm.business.uibeans;

import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SpringMailSender {
	
	private JavaMailSenderImpl mailSender = new org.springframework.mail.javamail.JavaMailSenderImpl();

	public boolean sendMail(SimpleMailMessage simpleMailMessage) {
		boolean sent = true;
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(simpleMailMessage.getFrom());
			helper.setTo(simpleMailMessage.getTo());
			if (simpleMailMessage.getCc() != null && simpleMailMessage.getCc().length > 0) {
				helper.setCc(simpleMailMessage.getCc());
			}
			helper.setSubject(simpleMailMessage.getSubject());
			helper.setText(String.format(simpleMailMessage.getText()), true);
			
			
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			
			mailSender.setHost("smtp.gmail.com");
			mailSender.setJavaMailProperties(props);
			mailSender.setUsername("admindesk@kstrata.com");
			mailSender.setPassword("kstrata123");
			mailSender.setPort(587);
			
			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			sent = false;
		}
		return sent;
		
		
	}
}
