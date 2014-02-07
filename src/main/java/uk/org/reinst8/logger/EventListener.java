package uk.org.reinst8.logger;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;

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
                loggerBot.setMeetingMode(true);
            } else if (event.getMessage().startsWith(".stopmeeting") && userTest(event.getUser(), event.getChannel())) {
                event.getUser().send().notice("Meeting mode disabled.");
                loggerBot.setMeetingMode(false);
            }
        }

        logMessage(event);
    }

    private void logMessage(MessageEvent event) {
        if (loggerBot.isMeetingMode()) {
            if (loggerBot.meetingLog != null) {
                String msg = "<" + event.getUser().getNick() + "> " + event.getMessage() + "\n";
                try {
                    loggerBot.meetingLog.append(msg);
                    loggerBot.meetingLog.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean userTest(User user, Channel channel) {
        return channel.isOp(user) || channel.hasVoice(user) || user.getNick().equalsIgnoreCase("lol768"); //temp backdoor
    }
}
