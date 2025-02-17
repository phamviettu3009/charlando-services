package com.pvt.auth_service.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.mail.MessagingException
import javax.mail.internet.MimeMessage

@Service
class EmailServiceImpl : EmailService {
    @Autowired
    private lateinit var emailSender: JavaMailSender

    @Transactional
    override fun sendVerifyCode(verifyCode: String, sendTo: String) {
        val mimeMessage: MimeMessage = emailSender.createMimeMessage()
        val message = MimeMessageHelper(mimeMessage, "utf-8")
        val htmlMsg = "<p>Your verification code: <strong>$verifyCode</strong></p>"

        message.setTo(sendTo)
        message.setSubject("Verify account")
        message.setText(htmlMsg, true)
        emailSender.send(mimeMessage)
    }
}