package edu.kulikov.email2telegram.bot.state;

import com.google.common.cache.Cache;
import com.google.common.eventbus.EventBus;
import edu.kulikov.email2telegram.bot.state.router.Router;
import edu.kulikov.email2telegram.bot.state.session.Session;
import edu.kulikov.email2telegram.bot.util.ListKeyboard;
import edu.kulikov.email2telegram.bot.util.UserUtil;
import edu.kulikov.email2telegram.cache.InternalStateCache;
import edu.kulikov.email2telegram.domain.entity.*;
import edu.kulikov.email2telegram.domain.service.MailboxAccountService;
import edu.kulikov.email2telegram.domain.service.ProviderService;
import edu.kulikov.email2telegram.email.connection.impl.AccountRegistrator;
import edu.kulikov.email2telegram.email.connection.oauth.OAuth2Provider;
import edu.kulikov.email2telegram.email.connection.oauth.OAuth2ProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.kulikov.email2telegram.bot.state.Constants.*;
import static edu.kulikov.email2telegram.bot.state.StateProvider.getStates;
import static edu.kulikov.email2telegram.bot.state.router.ContainsPredicate.contains;
import static edu.kulikov.email2telegram.bot.state.router.RegexpPredicate.regExp;
import static edu.kulikov.email2telegram.bot.state.session.SessionUtil.addFlash;
import static edu.kulikov.email2telegram.bot.state.session.SessionUtil.setUnknownValue;
import static edu.kulikov.email2telegram.spring.MessagesResolver.msg;
import static java.util.Collections.singletonList;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 10.09.2016
 */
@Component
@Slf4j
public class AddMailbox_TokenState extends AbstractRouterState {
    private EventBus eventBus;
    private Cache<String, Object> cache;
    private OAuth2ProviderFactory oAuth2ProviderFactory;
    private MailboxAccountService mailboxAccountService;
    private ProviderService providerService;
    private AccountRegistrator registrator;


    @PostConstruct
    private void registerBusHandler() {
        eventBus.register(this);
    }

    @Override
    public void processRequest(Update in, Session session, AbsSender outSender) throws TelegramApiException {

        MailProviderType providerType = (MailProviderType) session.getParam(MAIL_PROVIDER_KEY);
        SendMessage out = toSameChat(in);

        String userState = String.valueOf(UUID.randomUUID().hashCode());
        cache.put(userState, true);
        OAuth2Provider provider = oAuth2ProviderFactory.get(providerType);
        if (provider != null) {
            try {
                String authLink = provider.getAuthLink(userState);
                out.setText(msg("mail.get_token", authLink));
                out.enableMarkdown(true);
                out.setReplyMarkup(new ListKeyboard(singletonList(msg("bot.back.command"))));
                out.disableWebPagePreview();
                outSender.sendMessage(out);
            } catch (OAuthSystemException ignore) {
            }
        }
    }


    @Override
    protected Router getResponseRouter(Update in) {
        router
                .addLocal(contains("/start"), (command, session) -> {
                    try {
                        command = URLDecoder.decode(command, "UTF-8");
                    } catch (UnsupportedEncodingException ignore) {
                    }
                    Pattern pattern = Pattern.compile("/start (.*)");
                    Matcher matcher = pattern.matcher(command);
                    String key = matcher.matches() ? matcher.group(1) : "";
                    {
                        try {
                            String code = (String) cache.getIfPresent(key);
                            if (code != null) {
                                cache.invalidate(key);
                                MailboxAccount account = createAccount(code, in, session);
                                if (account != null) {
                                    session.addParam(ACCOUNT_ID_KEY, account.getAccountId(), false);
                                    addFlash(session, STATE_TEXT_KEY, msg("mail.get_token.success", account.getMailbox()));
                                    registrator.registerAccount(account.getAccountId());

                                    return getStates().getSubscriptions_mainMenuState();
                                }
                            }
                        } catch (Exception e) {
                            log.error("Can't process token by link", e);
                            setUnknownValue(session, msg("mail.get_token.error", e.getMessage()));
                            return getStates().getAddMailBox_AccountTypeState();


                        }
                        return getStates().getAddMailBox_AccountTypeState();
                    }
                })
                .addLocal(regExp(msg("bot.back.parse_re")), (command, session) ->
                        getStates().getAddMailBox_AccountTypeState());
        return router;
    }


    private MailboxAccount createAccount(String authCode, Update in, Session session) throws OAuthSystemException, OAuthProblemException {
        MailProviderType providerType = (MailProviderType) session.getParam(MAIL_PROVIDER_KEY);
        OAuth2Provider oAuth2Provider = oAuth2ProviderFactory.get(providerType);
        Provider mailProvider = providerService.findByType(providerType);

        if (oAuth2Provider != null) {
            User user = getUser(in);
            MailboxAccount account = new MailboxAccount(mailProvider,
                    new TelegramUser(user.getId(), user.getUserName(), user.getFirstName())
            );
            account.getSubscriptions().add(new Subscription(getChat(in).getId(), UserUtil.getChatName(in), true));
            account = mailboxAccountService.doAuthAndSave(account, authCode);

            return account;
            //String text = msg("mail.get_token.success", account.getMailbox());
        }
        return null;
    }


    @Autowired
    void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Autowired
    void setCache(@InternalStateCache Cache<String, Object> cache) {
        this.cache = cache;
    }

    @Autowired
    void setOAuth2ProviderFactory(OAuth2ProviderFactory oAuth2ProviderFactory) {
        this.oAuth2ProviderFactory = oAuth2ProviderFactory;
    }

    @Autowired
    void setMailboxAccountService(MailboxAccountService mailboxAccountService) {
        this.mailboxAccountService = mailboxAccountService;
    }

    @Autowired
    void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }

    @Autowired
    void setRegistrator(AccountRegistrator registrator) {
        this.registrator = registrator;
    }
}

