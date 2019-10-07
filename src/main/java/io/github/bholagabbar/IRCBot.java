package io.github.bholagabbar;

import com.ullink.slack.simpleslackapi.SlackUser;

import static io.github.bholagabbar.Main.slackBot;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class IRCBot extends ListenerAdapter {
    
    @Override
    public void onMessage(MessageEvent event) {
    	onMessage(event.getChannel().getName(), event.getUser().getNick(), event.getMessage());
    }

	public void onMessage(String channel, String sender, /*String login, String hostname, */String message) {
        message = "<irc@" + sender + ">: " + message;
        if (!sender.equals(BotConstants.IRC_BOT_NAME)) {
            //No mention. Normal Message
//            if (!message.contains("@" + BotConstants.IRC_BOT_NAME)) {
                if (BotConstants.MODE == 1 || (BotConstants.MODE == 2 && messageContainsSlackUserToSend(message))) {
                    RelayMessage.sendMessageFromIRCToSlack(message);
                }
            /*} else {
                if (message.contains("slack_users")) {
                    Main.ircBot.sendMessage(BotConstants.IRC_CHANNEL, getSlackUserMessageToSendOnIRC());
                } else if (message.contains("ping")) {
                    Main.ircBot.sendMessage(BotConstants.IRC_CHANNEL, BotConstants.IRC_PING_MSG);
                } else {
                    Main.ircBot.sendMessage(BotConstants.IRC_CHANNEL, BotConstants.IRC_TP_MSG);
                }
            }*/
        }
    }

    public String getSlackUserMessageToSendOnIRC() {
        StringBuilder slackUserListMessage = new StringBuilder();
        slackUserListMessage.append("Entities on the slack channel " + BotConstants.SLACK_CHANNEL + " are: ");
        for (SlackUser slackUser : Main.slackBot.session.getUsers()) {
            slackUserListMessage.append(slackUser.getUserName() + ", ");
        }
        return slackUserListMessage.toString();
    }

    private boolean messageContainsSlackUserToSend(String message) {
        for (SlackUser slackUser : slackBot.session.getUsers()) {
            if (message.contains("@" + slackUser.getUserName())) {
                return true;
            }
        }
        return false;
    }
}