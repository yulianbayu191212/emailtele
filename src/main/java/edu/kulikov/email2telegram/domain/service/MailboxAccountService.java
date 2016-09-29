package edu.kulikov.email2telegram.domain.service;

import edu.kulikov.email2telegram.domain.entity.AccountToken;
import edu.kulikov.email2telegram.domain.entity.MailProviderType;
import edu.kulikov.email2telegram.domain.entity.MailboxAccount;
import edu.kulikov.email2telegram.domain.entity.Subscription;
import edu.kulikov.email2telegram.domain.repository.MailboxAccountRepository;
import edu.kulikov.email2telegram.email.connection.oauth.OAuth2Provider;
import edu.kulikov.email2telegram.email.connection.oauth.OAuth2ProviderFactory;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.token.OAuthToken;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 04.09.2016
 */
@Service
public class MailboxAccountService {
    private final MailboxAccountRepository mailboxAccountRepository;
    private OAuth2ProviderFactory providerFactory;

    @Autowired
    public MailboxAccountService(MailboxAccountRepository mailboxAccountRepository) {
        this.mailboxAccountRepository = mailboxAccountRepository;
    }

    public List<MailboxAccount> findAll() {
        return mailboxAccountRepository.findAll();
    }

    public MailboxAccount save(MailboxAccount mailboxAccount) {
        return mailboxAccountRepository.save(mailboxAccount);
    }

    public MailboxAccount findLightById(Long id) {
        return mailboxAccountRepository.findOne(id);
    }

    @Transactional
    public MailboxAccount findWithProviderById(Long id) {
        MailboxAccount mailboxAccount = mailboxAccountRepository.findOne(id);
        Hibernate.initialize(mailboxAccount.getProvider());
        return mailboxAccount;
    }

    @Transactional
    public MailboxAccount findWithSubscriptions(Long id) {
        MailboxAccount mailboxAccount = mailboxAccountRepository.findOne(id);
        Hibernate.initialize(mailboxAccount.getSubscriptions());
        return mailboxAccount;
    }

    @Transactional
    public MailboxAccount findWithOwnerById(Long id) {
        MailboxAccount account = mailboxAccountRepository.findOne(id);
        Hibernate.initialize(account.getOwner());
        return account;
    }

    @Transactional
    public MailboxAccount addSubscription(Long id, Subscription subscription) {
        MailboxAccount withSubscriptions = findWithSubscriptions(id);
        withSubscriptions.getSubscriptions().add(subscription);
        return save(withSubscriptions);
    }


    @Transactional
    public MailboxAccount doAuthAndSave(MailboxAccount account, String oAuthCode) throws OAuthProblemException, OAuthSystemException {
       // Hibernate.initialize(account.getProvider());
        MailProviderType providerType = account.getProvider().getType();
        OAuth2Provider provider = providerFactory.get(providerType);
        OAuthToken oAuthToken = provider.getAccessTokenByCode(oAuthCode);
        AccountToken accountToken = new AccountToken(
                oAuthToken.getAccessToken(),
                oAuthToken.getRefreshToken(),
                oAuthToken.getExpiresIn(),
                Instant.now().plus(oAuthToken.getExpiresIn(), ChronoUnit.SECONDS));
        account.setAccountToken(accountToken);
        account.setMailbox(provider.getLoginByAccessToken(oAuthToken.getAccessToken()));
        return save(account);
    }

    @Transactional
    public MailboxAccount refreshAccessToken(Long accountId) throws OAuthProblemException, OAuthSystemException {
        MailboxAccount account = findWithProviderById(accountId);
        OAuth2Provider provider = providerFactory.get(account.getProvider().getType());
        OAuthToken oAuthToken = provider.refreshAccessToken(account.getAccountToken().getRefreshToken());
        AccountToken newToken = new AccountToken(
                oAuthToken.getAccessToken(),
                account.getAccountToken().getRefreshToken(), //use old refresh token
                oAuthToken.getExpiresIn(),
                Instant.now().plus(oAuthToken.getExpiresIn(), ChronoUnit.SECONDS)
        );
        account.setAccountToken(newToken);
        return save(account);
    }


    public boolean isUserOwnMailbox(Integer telegramUserId, String emailAddress) {
        return mailboxAccountRepository.findByOwnerTelegramUserIdAndMailbox(telegramUserId, emailAddress) != null;
    }

    public List<MailboxAccount> findAllByUserId(Integer telegramUserId) {
        return mailboxAccountRepository.findByOwnerTelegramUserId(telegramUserId);
    }

    public MailboxAccount findByUserIdAndMailbox(Integer telegramUserId, String emailAddress) {
        return mailboxAccountRepository.findByOwnerTelegramUserIdAndMailbox(telegramUserId, emailAddress);
    }

    @Autowired
    public void setProviderFactory(OAuth2ProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }
}
