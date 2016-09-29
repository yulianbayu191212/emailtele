package edu.kulikov.email2telegram.bot.state;

import edu.kulikov.email2telegram.bot.state.router.Router;
import edu.kulikov.email2telegram.bot.state.session.Session;
import edu.kulikov.email2telegram.bot.util.ListKeyboard;
import edu.kulikov.email2telegram.domain.entity.MailboxAccount;
import edu.kulikov.email2telegram.domain.entity.Subscription;
import edu.kulikov.email2telegram.domain.service.MailboxAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.kulikov.email2telegram.bot.state.Constants.*;
import static edu.kulikov.email2telegram.bot.state.StateProvider.getStates;
import static edu.kulikov.email2telegram.bot.state.router.ContainsPredicate.contains;
import static edu.kulikov.email2telegram.bot.state.router.RegexpPredicate.regExp;
import static edu.kulikov.email2telegram.bot.state.session.SessionUtil.setUnknownValue;
import static edu.kulikov.email2telegram.spring.MessagesResolver.msg;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 21.09.2016
 */
@Slf4j
@Component
public class Subscriptions_MainMenuState extends AbstractRouterState {
    private MailboxAccountService accounts;

    @Override
    public void processRequest(Update in, Session session, AbsSender outSender) throws TelegramApiException {
        Long param = (Long) session.getParam(ACCOUNT_ID_KEY);
        SendMessage message = toSameChat(in);
        if (param == null) {
            log.error("can't process request without ACCOUNT_ID_KEY");
        } else {
            MailboxAccount account = accounts.findWithSubscriptions(param);
            List<Subscription> subscriptions = account.getSubscriptions();
            String stateText = (String) session.getParam(STATE_TEXT_KEY);
            if (stateText != null) {
                message.setText(stateText);
            } else {
                message.setText(msg("bot.subscribers.menu", account.getMailbox()));
            }
            ListKeyboard keyboard = new ListKeyboard(
                    concat(of(msg("bot.subscribers.new.command")),
                            subscriptions
                                    .stream().map(
                                    s -> msg("bot.subscribers.select.command", s.getChatName(),
                                            s.isActive() ?
                                                    msg("bot.subscribers.select.active") :
                                                    msg("bot.subscribers.select.inactive")))).
                            collect(toList()));
            message.setReplyMarkup(keyboard);
            outSender.sendMessage(message);
        }
    }

    @Override
    protected Router getResponseRouter(Update in) {
        return router.
                addLocal(contains(msg("bot.subscribers.new.parse")),
                        (command, session) -> getStates().getSubscriptions_addNewState()).
                addLocal(regExp(msg("bot.subscribers.parse_re")),
                        (command, session) -> {
                            Matcher matcher = Pattern.compile(msg("bot.subscribers.parse_re")).matcher(command);
                            matcher.matches();
                            session.addParam(SUBSCRIPTION_EDIT_FOR_CHAT_KEY, matcher.group(1), false);
                            return getStates().getSubscriptions_editState();
                        }).
                addLocal(regExp("bot.back.command"),
                        (command, session) -> getStates().getMailboxes_menuState()).
                setDefaultLocal((command, session) -> {
                    setUnknownValue(session, msg("bot.unknown_command"));
                    return this;
                });
    }

    @Override
    public String toString() {
        return "Subscriptions Main Menu state";
    }

    @Autowired
    public void setAccounts(MailboxAccountService accounts) {
        this.accounts = accounts;
    }
}
