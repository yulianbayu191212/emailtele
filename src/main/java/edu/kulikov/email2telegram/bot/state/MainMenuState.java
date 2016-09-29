package edu.kulikov.email2telegram.bot.state;

import com.google.common.cache.Cache;
import edu.kulikov.email2telegram.bot.state.router.Router;
import edu.kulikov.email2telegram.bot.state.router.RouterUtil;
import edu.kulikov.email2telegram.bot.state.session.Session;
import edu.kulikov.email2telegram.bot.util.ListKeyboard;
import edu.kulikov.email2telegram.domain.entity.MailboxAccount;
import edu.kulikov.email2telegram.domain.entity.Subscription;
import edu.kulikov.email2telegram.domain.service.MailboxAccountService;
import edu.kulikov.email2telegram.email.connection.impl.AccountRegistrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

import static edu.kulikov.email2telegram.bot.state.StateProvider.getStates;
import static edu.kulikov.email2telegram.bot.state.router.ContainsPredicate.contains;
import static edu.kulikov.email2telegram.bot.state.session.SessionUtil.getUnknownValue;
import static edu.kulikov.email2telegram.bot.state.session.SessionUtil.setUnknownValue;
import static edu.kulikov.email2telegram.bot.util.UserUtil.getChatName;
import static edu.kulikov.email2telegram.spring.MessagesResolver.msg;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 07.09.2016
 */
@Component
public class MainMenuState extends AbstractRouterState {
    private MailboxAccountService accountService;
    private AccountRegistrator registrator;
    @Value("${email2telegram.bot.name}")
    private String botName;
    private Cache<String, Object> cache;

    private static ReplyKeyboardMarkup mainMenuKeyboard(int mailboxCount) {
        List<String> menu = new ArrayList<>();
        menu.add(msg("bot.my_mailboxes.command", mailboxCount));
        menu.add(msg("bot.add_mailbox.command"));
        menu.add(msg("bot.is_it_safe.command"));
        return new ListKeyboard(menu);
    }

    @Autowired
    public void setAccountService(MailboxAccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void processRequest(Update in, Session session, AbsSender outSender) throws TelegramApiException {
        User user = getUser(in);
        SendMessage out = toSameChat(in);
        if (isGroupChat(in)) {
            if (in.getMessage().hasText() && in.getMessage().getText().contains("/start@" + botName)) {
                MailboxAccount mailboxAccount = addAccount(in, session);
                out.setText(msg("bot.subscribers.new.success", mailboxAccount.getMailbox()));
                outSender.sendMessage(out);
            }
        } else {
            String responseText = defaultIfNull(
                    getUnknownValue(session),
                    msg("bot.greeting", user.getFirstName()));
            out.setText(responseText);
            List<MailboxAccount> emails = accountService.findAllByUserId(user.getId());
            out.setReplyMarkup(mainMenuKeyboard(emails.size()));
            outSender.sendMessage(out);
        }

    }

    private MailboxAccount addAccount(Update in, Session session) {
        if (in.hasMessage() && !in.getMessage().getChat().isUserChat()) {
            String payload = RouterUtil.getCommandPayload("/start@" + botName, in.getMessage().getText());
            Long accParamId = (Long) cache.getIfPresent(payload);
            if (accParamId != null) {
                return accountService.save(accountService.addSubscription(accParamId, new Subscription(
                        getChat(in).getId(), getChatName(in), false))
                );
            }
        }
        setUnknownValue(session, msg("bot.unknown_command"));
        return null;
    }

    @Override
    protected Router getResponseRouter(Update in) {
        return router.
                addLocal(contains("/start@" + botName), (command, session) -> {
                    addAccount(in, session);
                    return this;
                }).
                addLocal(contains(msg("bot.my_mailboxes.parse")), (m, s) -> getStates().getMailboxes_menuState()).
                addLocal(contains(msg("bot.add_mailbox.parse")), (m, s) -> getStates().getAddMailBox_AccountTypeState()).
                addLocal(contains(msg("bot.is_it_safe.parse")), (m, s) -> null).
                setDefaultLocal((command, session) -> {
                    setUnknownValue(session, msg("bot.unknown_command"));
                    return this;
                });
    }


    @Override
    public String toString() {
        return "Main Menu state";
    }

    @Autowired
    void setCache(Cache<String, Object> cache) {
        this.cache = cache;
    }

    @Autowired
    void setRegistrator(AccountRegistrator registrator) {
        this.registrator = registrator;
    }
}
