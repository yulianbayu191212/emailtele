package edu.kulikov.email2telegram.spring;

import org.junit.Test;

import static edu.kulikov.email2telegram.spring.MessagesResolver.msg;
import static org.junit.Assert.assertEquals;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 24.09.2016
 */
public class MessagesResolverTest {

    @Test
    public void msg_simpleTest() throws Exception {
        assertEquals("From:", msg("mail.from"));

    }

    @Test
    public void msg_withEmoji() {
        assertEquals("➕ Add mailbox", msg("bot.add_mailbox.command"));
    }

    @Test
    public void msg_withReplacer() {
        assertEquals("\uD83D\uDCEE My mailboxes (1)", msg("bot.my_mailboxes.command", 1));
    }

}