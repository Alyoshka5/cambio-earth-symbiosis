package com.cambio_earth.symbiosis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String apiKey;

    @Value("${SUPPORT_EMAIL}")
    private String supportEmail;

    @Async
    public void sendVerificationEmail(String to, String subject, String text) throws ResendException {

        Resend resend = new Resend(apiKey);
        CreateEmailOptions params = CreateEmailOptions.builder()
            .from(supportEmail)
            .to(to)
            .subject(subject)
            .html(text)
            .build();

        resend.emails().send(params);
    }
}