package edu.kulikov.email2telegram.email.connection.api;

import javax.mail.Message;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 14.09.2016
 */
public interface MailboxListener {
    default void onConnect(Long accountId) {}
    default void onNewMail(Long accountId, Message message) {}
    default void onDisconnect(Long accountId, boolean manually) {}
    default void onAuthFail(Long accountId) {}
    default void onError(Long accountId, Exception ex) {}
}
