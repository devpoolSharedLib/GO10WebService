package th.co.gosoft.util;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailUtils {

    private static final String ENCODING = "UTF-8";
    private static final String DEFAULT_SMTP_SERVER_HOST = "tarmg.cpall.co.th";

    private EmailUtils() {
    }

    private static String getSTMPHost() {
        return DEFAULT_SMTP_SERVER_HOST;
    }

    public static void send(String from, String to, String cc, String bcc, String subject, String message, String attachFilePath) {
        String smtpHost = getSTMPHost();
        Properties props = System.getProperties();
        props.put("mail.smtp.host", smtpHost.trim());
        Session session = Session.getInstance(props);
        session.setDebug(false);
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from.trim()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to.trim(), false));
            msg.setSubject(subject, ENCODING);
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(message, ENCODING);
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);

            if (attachFilePath != null && attachFilePath.length() > 0) {
                MimeBodyPart mbp2 = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(attachFilePath);
                mbp2.setDataHandler(new DataHandler(fds));
                mbp2.setFileName(fds.getName());
                mp.addBodyPart(mbp2);
            }
            msg.setContent(mp);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            session = null;
        }
    }

    public static void send(String from, String to, String cc, String bcc, String subject, String message) {
        send(from, to, cc, bcc, subject, message, null);
    }
}