package uk.org.reinst8.logger;

import org.apache.commons.lang3.StringEscapeUtils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EventListener extends ListenerAdapter {

    private LoggerBot loggerBot;

    public EventListener(LoggerBot loggerBot) {
        this.loggerBot = loggerBot;
    }

    public void onMessage(MessageEvent event) throws Exception {
        System.out.println("WOO! " + event.getMessage());
        if (event.getChannel().getName().equalsIgnoreCase("#reinstate") || true) {
            if (event.getMessage().startsWith(".startmeeting") && userTest(event.getUser(), event.getChannel())) {
                event.getUser().send().notice("Meeting mode enabled.");
                loggerBot.setNick("Reinst8|Logging");
                loggerBot.setMeetingMode(true);
                appendMessage(getHtmlMessageLine("System", "Meeting initiated by " + event.getUser().getNick(), event.getChannel()), loggerBot.meetingLog);
                return;
            } else if (event.getMessage().startsWith(".stopmeeting") && userTest(event.getUser(), event.getChannel())) {
                event.getUser().send().notice("Meeting mode disabled.");
                appendMessage(getHtmlMessageLine("System", "Meeting terminated by " + event.getUser().getNick(), event.getChannel()), loggerBot.meetingLog);
                loggerBot.setNick("Reinst8|Log");
                loggerBot.setMeetingMode(false);
                return;
            }
        }

        logMessage(event);
    }

    private void logMessage(MessageEvent event) {
        if (loggerBot.isMeetingMode()) {
            if (loggerBot.meetingLog != null) {
                appendMessage(getHtmlMessageLine(event.getUser(), event.getMessage(), event.getChannel()), loggerBot.meetingLog);
            }
        }
    }

    private String getHtmlMessageLine(Object user, String message, Channel channel) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String time = dateFormat.format(new Date());
        String htmlString = "<span class=\"chatLine\"><span class=\"chatTime\">" + time + "</span>";
        message = StringEscapeUtils.escapeHtml4(message);
        if (user instanceof String) {
            user = StringEscapeUtils.escapeHtml4((String) user);
            htmlString += " <span class=\"chatNick nickSystem\">&lt;" + user + "&gt;</span>";
        } else if (user instanceof User) {
            User objectUser = (User) user;
            String attrs = (objectUser.getChannelsOpIn().contains(channel)) ? "@" : "";
            attrs += (objectUser.getChannelsVoiceIn().contains(channel)) ? "+" : "";

            String classes = "chatNick nickUser";
            classes += (objectUser.getChannelsOpIn().contains(channel)) ? " nickOp" : "";
            classes += (objectUser.getChannelsVoiceIn().contains(channel)) ? " nickVoice" : "";

            htmlString += " <span class=\"" + classes + "\">&lt;" + attrs + objectUser.getNick() + "&gt;</span>";
        } else {
            throw new UnsupportedOperationException("You must supply a String or User user object.");
        }

        htmlString += " <span class=\"chatMessage\">" + message + "</span></span>";
        return htmlString;
    }

    private void appendMessage(String msg, BufferedWriter writer) {
        try {
            writer.append(msg).append("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean userTest(User user, Channel channel) {
        return channel.isOp(user) || channel.hasVoice(user) || user.getNick().equalsIgnoreCase("lol768"); //temp backdoor
    }
}
