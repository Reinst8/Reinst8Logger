package uk.org.reinst8.logger;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LoggerBot {

    private boolean meetingMode = false;
    public BufferedWriter meetingLog;


    public LoggerBot() {
        Configuration configuration = new Configuration.Builder()
                .setName("Reinst8|Log")
                .setRealName("Reinst8|Log")
                .setLogin("LoggerBot")
                .setAutoNickChange(true)
                .setCapEnabled(true)
                .setNickservPassword(System.getenv("LOGGER-NSP"))
                .setCapEnabled(false)
                .addListener(new EventListener(this))
                .setServerHostname(System.getenv("LOGGER-HOST"))
                .setServerPort(Integer.parseInt(System.getenv("LOGGER-PORT")))
                .setServerPassword(System.getenv("LOGGER-SERVERPASS"))
                .addAutoJoinChannel("#reinstate")
                .buildConfiguration();
        try {

            PircBotX bot = new PircBotX(configuration);
            bot.startBot();

        } catch (IrcException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setMeetingMode(boolean meetingMode) {
        if (meetingMode) {
            File logFile = new File("log-meeting.log");
            try {
                FileWriter fileWriter = new FileWriter(logFile, true);
                this.meetingLog = new BufferedWriter(fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        this.meetingMode = meetingMode;
    }

    public boolean isMeetingMode() {
        return meetingMode;
    }

    public static void main(String[] args) {
        if (System.getenv("LOGGER-NSP") == null) {
            System.out.print("You must set the env variables: LOGGER-NSP, LOGGER-HOST, LOGGER-PORT, LOGGER-SERVERPASS");
            System.exit(-1);
        }
        new LoggerBot();
    }


}
