package com.iv.gravity.service.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import com.google.common.collect.ImmutableSet;

public class MailSender {

   private final static Properties mailProperties = mailProperties();

   public boolean sendMail(String receiverMail, String subject, Set<String> cc, String mailContent, List<File> attachments)
      throws MessagingException {
      JavaMailSender mailSender = mailSender();
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setTo(receiverMail);
      helper.setFrom(mailProperties.getProperty("mail.username"));
      helper.setSubject(subject);

      ImmutableSet<String> bcc = ImmutableSet.of("sankarmuthuganesh.r@ivtlinfoview.co.jp");
      helper.setBcc(bcc.toArray(new String[0]));

      if (CollectionUtils.isNotEmpty(cc)) {
         helper.setCc(cc.toArray(new String[0]));
      }
      helper.setText(mailContent);
      if (CollectionUtils.isNotEmpty(attachments)) {
         attachments.stream().filter(action -> action.isFile()).forEach(attachment -> {
            try {
               helper.addAttachment(attachment.getName(), attachment);
            }
            catch (MessagingException messagingException) {
            }
         });
      }

      mailSender.send(message);
      return true;
   }

   static JavaMailSender mailSender() {
      JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
      mailSender.setDefaultEncoding("UTF-8");
      mailSender.setHost(mailProperties.getProperty("mail.host"));
      mailSender.setPort((Integer.parseInt(mailProperties.getProperty("mail.port"))));
      mailSender.setUsername(mailProperties.getProperty("mail.username"));
      mailSender.setPassword(mailProperties.getProperty("mail.password"));
      mailSender.setJavaMailProperties(mailProperties);
      return mailSender;
   }

   private static Properties mailProperties() {
      Properties globalProperty = new Properties();
      String path = StringUtils.EMPTY;
      String directory = StringUtils.EMPTY;
      FileInputStream fileStream = null;
      try {
         directory = System.getenv("APPLICATION_DIR");
         if (directory.charAt(directory.length() - 1) != File.separatorChar) {
            directory = directory + File.separator;
         }
         path = directory + "mail.properties";

         fileStream = new FileInputStream(path);
         globalProperty.load(fileStream);

      }
      catch (IOException ioException) {
      }

      return globalProperty;

   }

}
