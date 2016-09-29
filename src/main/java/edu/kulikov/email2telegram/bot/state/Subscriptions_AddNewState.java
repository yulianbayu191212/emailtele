package edu.kulikov.email2telegram.bot.state;

import com.google.common.cache.Cache;
import edu.kulikov.email2telegram.bot.state.session.Session;
import edu.kulikov.email2telegram.bot.util.ListKeyboard;
import edu.kulikov.email2telegram.cache.InternalStateCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.UUID;

import static edu.kulikov.email2telegram.bot.state.Constants.ACCOUNT_ID_KEY;
import static edu.kulikov.email2telegram.bot.state.StateProvider.getStates;
import static edu.kulikov.email2telegram.spring.MessagesResolver.msg;
import static java.util.Collections.singletonList;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 25.09.2016
 */
@Component
public class Subscriptions_AddNewState extends AbstractState {
    private Cache<String, Object> cache;

    @Value("${email2telegram.bot.name}")
    private String botName;

    @Override
    public void processRequest(Update in, Session session, AbsSender outSender) throws TelegramApiException {
        String uuid = UUID.randomUUID().toString();
        String key = String.valueOf(uuid.hashCode());
        cache.put(key, session.getParam(ACCOUNT_ID_KEY));
        outSender.sendMessage(toSameChat(in).setText(
                msg("bot.subscribers.group_link",
                        "https://telegram.me/" + botName + "?startgroup=" + key)).
                enableMarkdown(true).
                setReplyMarkup(new ListKeyboard(singletonList(msg("bot.back.command")))).
                disableWebPagePreview());
    }

    @Override
    public State processResponse(Update in, Session session) {
        return getStates().getMainMenuState();
    }

    @Autowired
    @InternalStateCache
    public void setCache(Cache<String, Object> cache) {
        this.cache = cache;
    }
}
