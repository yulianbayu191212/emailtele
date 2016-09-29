package edu.kulikov.email2telegram.email.connection.impl;

import com.google.common.eventbus.EventBus;
import edu.kulikov.email2telegram.bus.NewMailEvent;
import edu.kulikov.email2telegram.domain.entity.EmailMessage;
import edu.kulikov.email2telegram.domain.entity.MailboxAccount;
import edu.kulikov.email2telegram.domain.service.MailboxAccountService;
import edu.kulikov.email2telegram.email.connection.api.AccountConnectionManager;
import edu.kulikov.email2telegram.email.connection.api.MailboxConnection;
import edu.kulikov.email2telegram.email.connection.api.MailboxListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import static edu.kulikov.email2telegram.email.util.HtmlExtractor.parseForMarkdown;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 21.09.2016
 */
@Component
@Slf4j
public class AccountRegistrator {
    private AccountConnectionManager accountConnectionManager;
    private MailboxAccountService mailboxAccountService;
    private EventBus eventBus;

    public void registerAccount(Long accountId) {
        try {
            MailboxConnection mailboxConnection = accountConnectionManager.registerAccount(accountId);
            mailboxConnection.addMailboxListener(new MailboxListener() {
                @Override
                public void onConnect(Long accountId) {
                    log.info("[Listener][onConnect] New connection for accountId={}", accountId);
                }

                @Override
                public void onNewMail(Long accountId, Message message) {
                    log.info("[Listener][onNewMail] New mail for accountId={}", accountId);
                    try {
                        MimeMessageParser parser = new MimeMessageParser((MimeMessage) message);
                        parser.parse();
                        String from = parser.getFrom();
                        String subject = parser.getSubject();
                        String content = null;
                        if (parser.hasPlainContent()) {
                            content = parser.getPlainContent();
                        } else if (parser.hasHtmlContent()) {
                            content = parseForMarkdown(parser.getHtmlContent());
                        }
                        EmailMessage emailMessage = new EmailMessage(from, subject, content);
                        eventBus.post(new NewMailEvent(accountId, emailMessage));
                    } catch (Exception e) {
                        log.error("[Listener][onNewMail] Error while parsing mail for accountId={}", accountId, e);
                        eventBus.post(new NewMailEvent(accountId, null));
                    }
                }
            });
            mailboxConnection.connect();
        } catch (Exception e) {
            log.error("[Registrator] Can't register account with id={}", accountId, e);
        }
    }

    @PostConstruct
    void registerOnStartup() {
        log.info("[Registrator] Register accounts on startup...");
        for (MailboxAccount account : mailboxAccountService.findAll()) {
            registerAccount(account.getAccountId());
        }
    }

    @Autowired
    public void setAccountConnectionManager(AccountConnectionManager accountConnectionManager) {
        this.accountConnectionManager = accountConnectionManager;
    }

    @Autowired
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Autowired
    public void setMailboxAccountService(MailboxAccountService mailboxAccountService) {
        this.mailboxAccountService = mailboxAccountService;
    }
}
