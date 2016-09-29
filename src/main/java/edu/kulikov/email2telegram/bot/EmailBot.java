package edu.kulikov.email2telegram.bot;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import edu.kulikov.email2telegram.bot.state.State;
import edu.kulikov.email2telegram.bot.state.StatePhase;
import edu.kulikov.email2telegram.bot.state.session.Session;
import edu.kulikov.email2telegram.bus.NewMailEvent;
import edu.kulikov.email2telegram.domain.entity.EmailMessage;
import edu.kulikov.email2telegram.domain.entity.MailboxAccount;
import edu.kulikov.email2telegram.domain.entity.Subscription;
import edu.kulikov.email2telegram.domain.service.MailboxAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import javax.annotation.PostConstruct;

import static edu.kulikov.email2telegram.spring.MessagesResolver.msg;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 07.09.2016
 */
@Component
public class EmailBot extends TelegramLongPollingBot {
    private static Logger logger = LoggerFactory.getLogger(EmailBot.class);
    private UserContextManager userContextManager;
    private MailboxAccountService accountService;

    private EventBus eventBus;

    private String botName;
    private String botToken;

    @Autowired
    public void setUserContextManager(UserContextManager userContextManager) {
        this.userContextManager = userContextManager;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Integer userId = getUserId(update);
                UserContext userContext = userContextManager.get(userId);
                State state = userContext.getState();
                Session session = userContext.getSession();
                StatePhase statePhase = userContext.getStatePhase();

                if (statePhase == StatePhase.REQUEST) {
                    logger.debug("Process request for userId={}, state={}",
                            userId, state);
                    state.processRequest(update, session, this);
                    userContext.setStatePhase(StatePhase.RESPONSE);
                } else {
                    logger.debug("Process response for userId={}, state={}",
                            userId, state);
                    State newState = state.processResponse(update, session);

                    userContext.setState(newState);
                    userContext.setStatePhase(StatePhase.REQUEST);
                    logger.debug("{} -> {}", state, newState);
                    onUpdateReceived(update);
                }
            }
        } catch (Exception e) {
            logger.error("Exception while processing update", e);
        }
    }

    @PostConstruct
    private void registerBusHandler() {
        eventBus.register(this);
    }

    @Subscribe
    public void onNewMailEvent(NewMailEvent event) throws TelegramApiException, InterruptedException {
        EmailMessage message = event.getMessage();
        StringBuilder res = new StringBuilder();
        if (message != null) {
            res.
                    append(msg("mail.from")).append(" ").append(message.getFrom()).append("\n").
                    append(msg("mail.subject")).append(" ").append(message.getSubject()).append("\n").
                    append(msg("mail.body")).append(" ").append(message.getContent());
        } else {
            res.append(msg("mail.error.cannot_parse"));
        }
        try {
            MailboxAccount account = accountService.findWithSubscriptions(event.getAccountId());
            for (Subscription subscription : account.getSubscriptions()) {
                sendMessage(new SendMessage().
                        setText(res.toString()).
                        setChatId(subscription.getTelegramChatId().toString()));
                Thread.sleep(2000); //dirty hack for delay, I now
            }

        } catch (TelegramApiException ex) {
            logger.error("[Telegram] Error while sending message", ex);
        }
    }


    //TODO: extract to utils
    private Integer getUserId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }
        return null;
    }

    @Autowired
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Autowired
    public void setAccountService(MailboxAccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Value("${email2telegram.bot.token}")
    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    @Value("${email2telegram.bot.name}")
    public void setBotName(String botName) {
        this.botName = botName;
    }
}
