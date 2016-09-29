package edu.kulikov.email2telegram.bot.state;

import edu.kulikov.email2telegram.bot.state.router.Router;
import edu.kulikov.email2telegram.bot.state.session.Session;
import edu.kulikov.email2telegram.bot.util.ListKeyboard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import static edu.kulikov.email2telegram.bot.state.StateProvider.getStates;
import static edu.kulikov.email2telegram.bot.state.router.RegexpPredicate.regExp;
import static edu.kulikov.email2telegram.bot.state.session.SessionUtil.getUnknownValue;
import static edu.kulikov.email2telegram.bot.state.session.SessionUtil.setUnknownValue;
import static edu.kulikov.email2telegram.domain.entity.MailProviderType.*;
import static edu.kulikov.email2telegram.spring.MessagesResolver.msg;
import static java.util.Arrays.asList;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 07.09.2016
 */
@Component
@Slf4j
public class AddMailbox_AccountTypeState extends AbstractRouterState {
    @Override
    public void processRequest(Update in, Session session, AbsSender outSender) throws TelegramApiException {
        SendMessage out = toSameChat(in);
        String unknownValue = getUnknownValue(session);
        if (unknownValue != null) {
            out.setText(unknownValue);
        } else {
            out.setText(msg("bot.add_mailbox.select_provider"));
        }
        out.setReplyMarkup(new ListKeyboard(
                asList("GMail", "Yandex", "Mail.Ru", msg("bot.back.command"))));
        outSender.sendMessage(out);
    }

    @Override
    protected Router getResponseRouter(Update in) {
        return router.
                addLocal(regExp("[Gg][Mm]ail"), (command, session) -> {
                    session.addParam(Constants.MAIL_PROVIDER_KEY, GMAIL, false);
                    return getStates().getAddMailbox_tokenState();
                }).
                addLocal(regExp("[Yy]andex"), (command, session) -> {
                    session.addParam(Constants.MAIL_PROVIDER_KEY, YANDEX, false);
                    return getStates().getAddMailbox_tokenState();
                }).
                addLocal(regExp("[Mm]ail\\.ru"), (command, session) -> {
                    session.addParam(Constants.MAIL_PROVIDER_KEY, MAIL_RU, false);
                    return getStates().getAddMailbox_tokenState();
                })
                .addLocal(regExp(msg("bot.back.parse_re")), (command, session) -> getStates().getMainMenuState())
                .setDefaultLocal((command, session) -> {
                    setUnknownValue(session, msg("bot.add_mailbox.unknown_provider"));
                    log.warn("User requested new mail provider: {}", command);
                    return this;
                });
    }


    @Override
    public String toString() {
        return "Add Mailbox state";
    }

}
