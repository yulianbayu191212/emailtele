package edu.kulikov.email2telegram.bot.state;

import edu.kulikov.email2telegram.bot.state.session.Session;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 25.09.2016
 */
@Component
public class MyMailboxes_MailboxState extends AbstractState {


    @Override
    public void processRequest(Update in, Session session, AbsSender outSender) throws TelegramApiException {
        SendMessage sendMessage = toSameChat(in);
        outSender.sendMessage(sendMessage.setText("Еще не реализовано"));
    }

    @Override
    public State processResponse(Update in, Session session) {
        return StateProvider.getStates().getMainMenuState();
    }
}
