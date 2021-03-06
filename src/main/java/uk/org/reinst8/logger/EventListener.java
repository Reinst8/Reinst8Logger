package uk.org.reinst8.logger;

import org.apache.commons.lang3.StringEscapeUtils;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
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
        if (event.getChannel().getName().equalsIgnoreCase("#reinst8")) {
            if (event.getMessage().startsWith(".startmeeting") && userTest(event.getUser(), event.getChannel())) {
                if (loggerBot.isMeetingMode()) {
                    event.getUser().send().notice("Meeting mode is already enabled.");
                } else {

                    event.getUser().send().notice("Meeting mode enabled.");
                    loggerBot.setNick("Reinst8|Logging");
                    loggerBot.setMeetingMode(true);
                    appendMessage(getHtmlMessageLine("System", "Meeting initiated by " + event.getUser().getNick() + ".", event.getChannel(), false), loggerBot.meetingLog);
                    return;
                }
            } else if (event.getMessage().startsWith(".stopmeeting") && userTest(event.getUser(), event.getChannel())) {
                if (!loggerBot.isMeetingMode()) {
                    event.getUser().send().notice("Meeting mode is already disabled.");
                } else {
                    event.getUser().send().notice("Meeting mode disabled.");
                    appendMessage(getHtmlMessageLine("System", "Meeting terminated by " + event.getUser().getNick() + ".", event.getChannel(), false), loggerBot.meetingLog);
                    loggerBot.setNick("Reinst8Bot");
                    loggerBot.setMeetingMode(false);
                    return;
                }
            }
        }

        logMessage(event);
    }

    public void onAction(ActionEvent event) {
        if (event.getChannel().getName().equalsIgnoreCase("#reinst8")) {
            System.out.println("Got action! " + event.getMessage());
            logAction(event);
        }
    }

    private void logMessage(MessageEvent event) {
        if (loggerBot.isMeetingMode()) {
            if (loggerBot.meetingLog != null && event.getMessage() != null) {
                appendMessage(getHtmlMessageLine(event.getUser(), event.getMessage(), event.getChannel(), false), loggerBot.meetingLog);
            }
        }
    }

    private void logAction(ActionEvent event) {
        if (loggerBot.isMeetingMode()) {
            if (loggerBot.meetingLog != null && event.getMessage() != null) {
                appendMessage(getHtmlMessageLine(event.getUser(), event.getMessage(), event.getChannel(), true), loggerBot.meetingLog);
            }
        }
    }

    private String getHtmlMessageLine(Object user, String message, Channel channel, boolean action) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date currentDate = new Date();
        String time = dateFormat.format(currentDate);
        dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        message = message.replace("%DATE%", dateFormat.format(currentDate));
        String htmlString = "<span class=\"chatLine\"><span class=\"chatTime\">" + time + "</span>";
        if (action) {
            htmlString += " <span class=\"actionMessage\">*";
        }
        message = message.replace("\u0002", "*");
        message = message.replace("\u001D", "/");
        message = message.replace("\u001F", "_");
        message = StringEscapeUtils.escapeHtml4(Colors.removeFormattingAndColors(message));
        if (user instanceof String) {
            user = StringEscapeUtils.escapeHtml4((String) user);
            if (!action) {
                htmlString += " <span class=\"chatNick nickSystem\">&lt;" + user + "&gt;</span>";
            } else {
                htmlString += " <span class=\"chatNick nickSystem\">" + user + "</span>";
            }
        } else if (user instanceof User) {
            User objectUser = (User) user;
            String attrs = (objectUser.getChannelsOpIn().contains(channel)) ? "@" : "";
            attrs += (objectUser.getChannelsVoiceIn().contains(channel)) ? "+" : "";

            String classes = "chatNick nickUser";
            classes += (objectUser.getChannelsOpIn().contains(channel)) ? " nickOp" : "";
            classes += (objectUser.getChannelsVoiceIn().contains(channel)) ? " nickVoice" : "";
            if (!action) {
                htmlString += " <span class=\"" + classes + "\">&lt;" + attrs + objectUser.getNick() + "&gt;</span>";
            } else {
                htmlString += " <span class=\"" + classes + "\">" + attrs + objectUser.getNick() + "</span>";
            }
        } else {
            throw new UnsupportedOperationException("You must supply a String or User user object.");
        }

        htmlString += " <span class=\"chatMessage\">" + message + "</span></span>";
        if (action) {
            htmlString += "</span>";
        }
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
        return channel.isOp(user) || channel.hasVoice(user);
    }
}
