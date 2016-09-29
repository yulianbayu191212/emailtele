package edu.kulikov.email2telegram.email.connection.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import edu.kulikov.email2telegram.domain.entity.MailboxAccount;
import edu.kulikov.email2telegram.domain.entity.Provider;
import edu.kulikov.email2telegram.domain.service.MailboxAccountService;
import edu.kulikov.email2telegram.email.connection.api.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static edu.kulikov.email2telegram.email.util.TableUtil.first;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 17.09.2016
 */
@Slf4j
@Component
public class AccountConnectionManagerImpl implements AccountConnectionManager,
        ProviderHeartbeatListener, MailboxListener {
    private Table<Long, Provider, MailboxConnection> connections;
    private Set<Provider> problemProviders;
    private MailboxAccountService mailboxAccountService;
    private ProviderHeartbeatMonitor providerHeartbeatMonitor;


    public AccountConnectionManagerImpl() {
        connections = HashBasedTable.create();
        problemProviders = new HashSet<>();


    }

    @Override
    public MailboxConnection registerAccount(Long accountId) {
        if (connections.containsRow(accountId)) {
            throw new IllegalArgumentException("Account with id="+accountId+" is already registered!");
        }
        MailboxAccount account = mailboxAccountService.findWithProviderById(accountId);
        MailboxConnection connection = new MailboxConnectionImpl(account);
        connection.addMailboxListener(this);
        connections.put(accountId, account.getProvider(), connection);
        if (!providerHeartbeatMonitor.isStarted()) {
            providerHeartbeatMonitor.startMonitor(15, TimeUnit.SECONDS);
        }
        log.info("Connection registered for accountId={}", accountId);
        return connection;
    }

    @Override
    public void unregisterAccount(Long accountId) {
        if (connections.containsRow(accountId)) {
            Map<Provider, MailboxConnection> row = connections.row(accountId);
            //only one value for accountId
            Pair<Provider, MailboxConnection> first = first(row);
            first.getValue().disconnect();
            row.clear();
            log.info("Connection unregistered for accountId={}", accountId);
        }
    }

    @PostConstruct
    protected void onConstruct() {
        providerHeartbeatMonitor.addListener(this);
    }

    @PreDestroy
    protected void onDestroy() {
        connections.values().forEach(MailboxConnection::disconnect);
        providerHeartbeatMonitor.stopMonitor();
    }


    @Autowired
    public void setMailboxAccountService(MailboxAccountService mailboxAccountService) {
        this.mailboxAccountService = mailboxAccountService;
    }

    @Autowired
    public void setProviderHeartbeatMonitor(ProviderHeartbeatMonitor providerHeartbeatMonitor) {
        this.providerHeartbeatMonitor = providerHeartbeatMonitor;
    }


    @Override
    public void onHeartbeatFail(Provider provider, Exception e) {
        connections.column(provider).forEach((__, connection) -> connection.onProviderProblem(e));
        problemProviders.add(provider);
    }

    @Override
    public void onHeartbeatOk(Provider provider) {
        if (problemProviders.contains(provider)) {
            connections.column(provider).forEach(
                    (__, connection) -> connection.connect());
            problemProviders.remove(provider);
        }
    }


    @Override
    public void onAuthFail(Long accountId) {
        try {
            log.info("Auth fail for accountId={}, refresh token...", accountId);
            Set<MailboxListener> oldListeners = first(connections.row(accountId)).getValue().getMailboxListeners();
            unregisterAccount(accountId);
            mailboxAccountService.refreshAccessToken(accountId);
            log.info("Got new access token for accountId={}", accountId);
            registerAccount(accountId);
            MailboxConnection newConnection = first(connections.row(accountId)).getValue();
            oldListeners.forEach(newConnection::addMailboxListener);
            newConnection.connect();
        } catch (OAuthProblemException | OAuthSystemException e) {
            log.error("Can't refresh access token for accountId={}", accountId, e);
        }
    }


}
