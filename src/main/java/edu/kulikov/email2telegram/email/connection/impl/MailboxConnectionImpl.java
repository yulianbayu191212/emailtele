package edu.kulikov.email2telegram.email.connection.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import edu.kulikov.email2telegram.domain.entity.MailboxAccount;
import edu.kulikov.email2telegram.email.connection.api.MailboxConnection;
import edu.kulikov.email2telegram.email.connection.api.MailboxListener;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.event.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static edu.kulikov.email2telegram.email.connection.impl.MailboxConnectionImpl.State.*;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 14.09.2016
 */
@Slf4j
public class MailboxConnectionImpl implements MailboxConnection {
    private State state;
    private Set<MailboxListener> mailListeners;
    private MessageCountListener externalMsgCountListener;
    private IMAPStore imapStore;
    private Session session;
    private IMAPFolder folder;

    private MailboxAccount mailboxAccount;
    private ScheduledExecutorService idleService;
    private ScheduledExecutorService keepAliveService;

    protected MailboxConnectionImpl(MailboxAccount mailboxAccount) {
        this.mailboxAccount = mailboxAccount;
        mailListeners = new HashSet<>();
        state = INSTANTIATED;
        idleService = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().
                        setDaemon(true).
                        setNameFormat("Idle-Account-" +
                                mailboxAccount.getAccountId() + "-#%d").build());
        keepAliveService = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setNameFormat("Keep-Alive-Account-" +
                                mailboxAccount.getAccountId() + "-#%d").build());
    }

    public void connect() {
        if (state == CONNECTING || state == CONNECTED) return;
        state = CONNECTING;
        createSession();
        connectStore();
    }

    private void connectStore() {
        try {
            imapStore = ((IMAPStore) session.getStore(session.getProperty("mail.store.protocol")));
            imapStore.addConnectionListener(new ConnectionAdapter() {
                @Override
                public void opened(ConnectionEvent e) {
                    connectFolder("INBOX");
                    idle();
                }

                @Override
                public void disconnected(ConnectionEvent e) {
                    closeConnection();
                    toDisconnectedState(false);
                }
            });
            imapStore.connect(
                    mailboxAccount.getProvider().getHost(),
                    mailboxAccount.getProvider().getPort(),
                    mailboxAccount.getMailbox(),
                    mailboxAccount.getAccountToken().getAccessToken());
        } catch (NoSuchProviderException ignored) {
        } catch (AuthenticationFailedException e) {
            authFailEvent();
            state = CANNOT_CONNECT;
        } catch (Exception e) {
            errorEvent(e);
            state = CANNOT_CONNECT;
            closeConnection(); //need?
        }
    }

    private void connectFolder(String name) {
        try {
            closeFolder();
            folder = (IMAPFolder) imapStore.getFolder(name);
            openFolder();
        } catch (Exception e) {
            errorEvent(e);
        }
    }

    private void openFolder() {
        try {
            //remove all listeners
            externalMsgCountListener = new MessageCountAdapter() {
                @Override
                public void messagesAdded(MessageCountEvent e) {
                    for (Message message : e.getMessages()) {
                        mailListeners.forEach(l -> l.onNewMail(getAccountId(), message));
                    }
                }
            };
            folder.addMessageCountListener(externalMsgCountListener);
            folder.open(Folder.READ_ONLY);
            folder.setSubscribed(true);
            toConnectedState();
        } catch (MessagingException e) {
            errorEvent(e);
            state = CANNOT_CONNECT;
        }
    }


    private void closeFolder() throws MessagingException {
        if (folder == null || !folder.isOpen())
            return;
        idleService.shutdownNow(); //TODO: await termination?
        keepAliveService.shutdownNow();
        folder.removeMessageCountListener(externalMsgCountListener);
        folder.setSubscribed(false);
        folder.close(false);
        folder = null;
    }

    private void idle() {
        keepAliveService.scheduleWithFixedDelay(new KeepAliveRunnable(folder), 0, 5, MINUTES);
        idleService.scheduleWithFixedDelay(() -> {
            try {
                if (folder.isOpen()) {
                    folder.idle(false);
                }
            } catch (MessagingException e) {
                errorEvent(e);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }


    private void closeConnection() {
        if (imapStore == null || state == DISCONNECTED)
            return;
        try {
            closeFolder();
            if (imapStore.isConnected()) {
                imapStore.close();
            }
        } catch (MessagingException e) {
            errorEvent(e);
        }
    }

    public void disconnect() {
        closeConnection();
        toDisconnectedState(true);
    }

    @Override
    public void onProviderProblem(Exception exception) {
        toDisconnectedState(false);
    }

    @Override
    public void addMailboxListener(MailboxListener mailboxListener) {
        mailListeners.add(mailboxListener);
    }

    @Override
    public void removeMailboxListener(MailboxListener mailboxListener) {
        mailListeners.remove(mailboxListener);
    }

    @Override
    public Set<MailboxListener> getMailboxListeners() {
        return new HashSet<>(mailListeners);
    }

    @Override
    public void removeMailboxListeners() {
        mailListeners.clear();
    }

    @Override
    public Long getAccountId() {
        return mailboxAccount.getAccountId();
    }

    private void createSession() {
        Properties props = System.getProperties();
        String imapProtocol = "imap";
        if (mailboxAccount.getProvider().getSsl()) {
            //   imapProtocol = "imaps";
            props.setProperty("mail.imap.ssl.enable", "true");
            props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.imap.socketFactory.fallback", "false");
        }
        props.setProperty("mail.store.protocol", imapProtocol);
        props.put("mail.imap.auth.mechanisms", "XOAUTH2");
        props.put("mail.imap.timeout", "10000");
        session = Session.getDefaultInstance(props);
        // session.setDebug(true);
        //session.setDebugOut(System.out);
    }

    private void toConnectedState() {
        if (state == CONNECTING) {
            state = CONNECTED;
            mailListeners.forEach(l -> l.onConnect(getAccountId()));
        } else {
            log.warn("Can't switch to CONNECTED state from {} state", state);
        }
    }

    private void toDisconnectedState(boolean manually) {
        if (state == CONNECTED) {
            state = DISCONNECTED;
            mailListeners.forEach(l -> l.onDisconnect(getAccountId(), manually));
        } else {
            log.warn("Can't switch to DISCONNECTED state from {} state", state);
        }
    }


    private void errorEvent(Exception e) {
        mailListeners.forEach(l -> l.onError(getAccountId(), e));
    }

    private void authFailEvent() {
        mailListeners.forEach(l -> l.onAuthFail(getAccountId()));
    }

    enum State {
        INSTANTIATED, CONNECTING, CONNECTED, CANNOT_CONNECT, DISCONNECTED
    }

    @Slf4j
    private static class KeepAliveRunnable implements Runnable {

        private IMAPFolder folder;

        public KeepAliveRunnable(IMAPFolder folder) {
            this.folder = folder;
        }

        @Override
        public void run() {
            try {
                // Perform a NOOP just to keep alive the connection
                log.debug("[KeepAlive] Performing a NOOP");
                folder.doCommand(p -> {
                    p.simpleCommand("NOOP", null);
                    return null;
                });
                // Ignore, just aborting the idleThread...
            } catch (MessagingException e) {
                // Shouldn't really happen...
                log.warn("[KeepAlive] Unexpected exception", e);
            }
        }
    }

}
