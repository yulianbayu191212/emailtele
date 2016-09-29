package edu.kulikov.email2telegram.bot.state;

import edu.kulikov.email2telegram.bot.state.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import static edu.kulikov.email2telegram.bot.state.Constants.SUBSCRIPTION_EDIT_FOR_CHAT_KEY;
import static edu.kulikov.email2telegram.bot.state.StateProvider.getStates;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 25.09.2016
 */
@Component
public class Subscriptions_EditState extends AbstractState {
    @Override
    public void processRequest(Update in, Session session, AbsSender outSender) throws TelegramApiException {
        String chatName = (String) session.getParam(SUBSCRIPTION_EDIT_FOR_CHAT_KEY);
        if (chatName == null) throw new IllegalStateException(SUBSCRIPTION_EDIT_FOR_CHAT_KEY + " must be set");
        outSender.sendMessage(toSameChat(in).setText("Settings for "+chatName));
    }

    @Override
    public State processResponse(Update in, Session session) {
        return getStates().getMainMenuState();
    }
}
