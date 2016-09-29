package edu.kulikov.email2telegram.bot.util;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 25.09.2016
 */
public class UserUtil {
    public static String getChatName(Update in) {
        if (in.hasMessage()) {
            Chat chat = in.getMessage().getChat();
            if (chat.isUserChat()) return getFullName(in.getMessage().getFrom());
            if (chat.isGroupChat()||chat.isSuperGroupChat()) return in.getMessage().getChat().getTitle();
        }
        return null;
    }
    public static String getFullName(User user) {
        return user.getFirstName() +
                (user.getLastName() == null ? "" : " "+user.getLastName());
    }
}
