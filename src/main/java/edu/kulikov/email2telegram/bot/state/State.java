package edu.kulikov.email2telegram.bot.state;

import edu.kulikov.email2telegram.bot.state.session.Session;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 07.09.2016
 */
public interface State {
    void processRequest(Update in, Session session, AbsSender outSender) throws TelegramApiException;
    State processResponse(Update in, Session session);
}
