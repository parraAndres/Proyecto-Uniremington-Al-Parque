package com.vetsync.app.uniremington.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@uniremington.edu.co}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public void sendResetPasswordEmail(String toEmail, String nombre, String token) {
        String subject = "Recuperación de Contraseña - Uniremington al Parque";
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; padding: 20px; border-radius: 10px;'>" +
                "<div style='text-align: center; margin-bottom: 20px;'>" +
                "<h1 style='color: #00447b;'>Uniremington al Parque</h1>" +
                "</div>" +
                "<p>Hola <strong>" + nombre + "</strong>,</p>" +
                "<p>Hemos recibido una solicitud para restablecer tu contraseña. Si no fuiste tú, puedes ignorar este correo.</p>" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + resetUrl + "' style='background-color: #ea0a2a; color: white; padding: 12px 25px; text-decoration: none; font-weight: bold; border-radius: 5px; display: inline-block;'>Restablecer mi Contraseña</a>" +
                "</div>" +
                "<p>O copia y pega el siguiente enlace en tu navegador:</p>" +
                "<p style='word-break: break-all; color: #666;'>" + resetUrl + "</p>" +
                "<hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "<p style='font-size: 12px; color: #999; text-align: center;'>Este enlace expirará en 1 hora.<br>© 2025 Uniremington - Proyección Social</p>" +
                "</div>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (Exception e) {
            // MODO DESARROLLO / SIMULACIÓN (Gratis)
            // Si no hay un servidor SMTP configurado, imprimimos el enlace en la terminal
            System.out.println("\n==================================================");
            System.out.println("📩 [CORREO SIMULADO] RECUPERACIÓN DE CONTRASEÑA");
            System.out.println("Para: " + nombre + " (" + toEmail + ")");
            System.out.println("Enlace: " + resetUrl);
            System.out.println("==================================================\n");
        }
    }
}
