package io.github.bholagabbar;

import static io.github.bholagabbar.BotConstants.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.pircbotx.Configuration;
import org.pircbotx.Configuration.ServerEntry;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.SASLCapHandler;
import org.pircbotx.hooks.managers.BackgroundListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;

public class Main {

    public static PircBotX ircBot;
    public static SlackBot slackBot;

    private static void setupIRCBot() throws Exception {
    	
    	IRCBot listener = new IRCBot();
    	
    	ThreadedListenerManager  myListenerManager = new ThreadedListenerManager ();
    	myListenerManager.addListener(listener);

    	Configuration config = new Configuration.Builder()
    		    .setName(IRC_BOT_NAME) //Nick of the bot. CHANGE IN YOUR CODE
    		    .setLogin("PircBotXUser") //Login part of hostmask, eg name:login@host
    		    .addAutoJoinChannel(BotConstants.IRC_CHANNEL) //Join #pircbotx channel on connect
    		    .addCapHandler(new SASLCapHandler(BotConstants.IRC_BOT_NAME,BotConstants.IRC_PASSWORD))
    	    	.setListenerManager(myListenerManager)
    		    .buildForServer(BotConstants.IRC_SERVER, BotConstants.IRC_PORT); //Create an immutable configuration from this builder
    	
        ircBot = new PircBotX(config);
//        ircBot.setVerbose(true);
        ircBot.startBot();
        if (BotConstants.IRC_JOIN_MESSAGE_NOTIFICATION.equals("true")) {
//            ircBot.sendMessage(BotConstants.IRC_CHANNEL, BotConstants.IRC_JOIN_MSG);
            ircBot.getUserChannelDao().getChannel(BotConstants.IRC_CHANNEL).send().message("test join message");
        }
    }

    private static void setupSlackBot() throws Exception {
        slackBot = new SlackBot();
        slackBot.session.connect();
        slackBot.session.joinChannel(SLACK_CHANNEL);
        //Set Dependant Bot Constants
        BotConstants.setSlackChannelObject(slackBot.session.findChannelByName(SLACK_CHANNEL));
        BotConstants.setSlackTeamName(slackBot.session.getTeam().getName());
        BotConstants.setJoinIRCMessage();
        //Start
        slackBot.Listen();
        if (BotConstants.SLACK_JOIN_MESSAGE_NOTIFICATION.equals("true")) {
            slackBot.session.sendMessage(BotConstants.SLACK_CHANNEL_OBJECT, BotConstants.SLACK_JOIN_MSG);
        }
    }

    public static void main(String[] args) throws Exception {
        new BotConstants();
        setupSlackBot();
        setupIRCBot();
    }

}