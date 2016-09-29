package edu.kulikov.email2telegram.email.connection.api;

import java.util.Set;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 17.09.2016
 */
public interface MailboxConnection  {
    Long getAccountId();
    void connect();
    void disconnect();
    void onProviderProblem(Exception exception);

    void addMailboxListener(MailboxListener mailboxListener);
    void removeMailboxListener(MailboxListener mailboxListener);
    Set<MailboxListener> getMailboxListeners();
    void removeMailboxListeners();
}
