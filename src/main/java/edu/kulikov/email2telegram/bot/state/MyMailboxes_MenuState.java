package edu.kulikov.email2telegram.bot.state;

import edu.kulikov.email2telegram.bot.state.router.Router;
import edu.kulikov.email2telegram.bot.state.session.Session;
import edu.kulikov.email2telegram.bot.state.session.SessionUtil;
import edu.kulikov.email2telegram.bot.util.ListKeyboard;
import edu.kulikov.email2telegram.domain.entity.MailboxAccount;
import edu.kulikov.email2telegram.domain.service.MailboxAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.List;

import static edu.kulikov.email2telegram.bot.state.Constants.ACCOUNT_ID_KEY;
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
 * @date 25.09.2016
 */
@Component
public class MyMailboxes_MenuState extends AbstractRouterState {
    private MailboxAccountService accountService;

    @Override
    public void processRequest(Update in, Session session, AbsSender outSender) throws TelegramApiException {
        String unknownValue = SessionUtil.getUnknownValue(session);
        if (unknownValue != null) {
            outSender.sendMessage(toSameChat(in).setText(unknownValue));
            return; //user selected unknown option, keep the same keyboard
        }
        List<MailboxAccount> accounts = accountService.findAllByUserId(getUser(in).getId());
        SendMessage toSend = toSameChat(in);
        toSend.setText(accounts.isEmpty() ? msg("bot.no_mailbox") : msg("bot.my_mailboxes"));
        toSend.setReplyMarkup(new ListKeyboard(
                concat(of(msg("bot.add_mailbox.command")),
                        concat(accounts.stream().map(MailboxAccount::getMailbox),
                                of(msg("bot.back.command")))).collect(toList())));
        outSender.sendMessage(toSend);
    }

    @Override
    protected Router getResponseRouter(Update in) {
        return router.
                addLocal(contains(msg("bot.add_mailbox.parse")), (command, session) -> getStates().getAddMailBox_AccountTypeState()).
                addLocal(regExp(msg("bot.back.parse_re")), (command, session) -> getStates().getMainMenuState()
                ).
                setDefaultLocal((command, session) -> {
                    MailboxAccount mailboxAccount = accountService.findByUserIdAndMailbox(getUser(in).getId(), command);
                    if (mailboxAccount != null) {
                        session.addParam(ACCOUNT_ID_KEY,mailboxAccount.getAccountId(),false);
                        return getStates().getSubscriptions_mainMenuState();
                    }
                    setUnknownValue(session, msg("bot.unknown_command"));
                    return this;
                });
    }

    @Override
    public String toString() {
        return "My Mailboxes Menu state";
    }

    @Autowired
    public void setAccountService(MailboxAccountService accountService) {
        this.accountService = accountService;
    }
}
