package edu.kulikov.email2telegram.bot.state;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 07.09.2016
 */
public abstract class AbstractState implements State {
    protected Chat getChat(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChat();
        }
        return null;
    }

    protected User getUser(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom();
        }
        return null;
    }

    protected String getText(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText();
        }
        return null;
    }

    protected boolean isGroupChat(Update update) {
        if (update.hasMessage() ) {
            Chat chat = update.getMessage().getChat();
            return chat.isGroupChat() || chat.isSuperGroupChat();
        }
        return false;
    }

    protected SendMessage toSameChat(Update update) {
        Message message = update.getMessage();
        //TODO: check when message is null
        return new SendMessage().setChatId(message.getChatId().toString());
    }


}
