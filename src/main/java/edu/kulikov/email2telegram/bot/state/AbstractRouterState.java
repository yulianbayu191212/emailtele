package edu.kulikov.email2telegram.bot.state;

import edu.kulikov.email2telegram.bot.state.router.Router;
import edu.kulikov.email2telegram.bot.state.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.objects.Update;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 10.09.2016
 */
public abstract class AbstractRouterState extends AbstractState {
    protected Router router;

    protected abstract Router getResponseRouter(Update in);

    @Override
    public State processResponse(Update in, Session session) {
        String text = getText(in);
        if (text == null) text = "";
        return getResponseRouter(in).process(text, session);
    }

    @Autowired
    private void setRouter(Router router) {
        this.router = router;
    }


}
