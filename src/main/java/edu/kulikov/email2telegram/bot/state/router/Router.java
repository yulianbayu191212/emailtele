package edu.kulikov.email2telegram.bot.state.router;

import edu.kulikov.email2telegram.bot.state.State;
import edu.kulikov.email2telegram.bot.state.session.Session;

import java.util.function.Predicate;

/**
 * 1. local
 * 2. global
 * 3. default local
 *
 * @author Andrey Kulikov (ankulikov)
 * @date 10.09.2016
 */
public interface Router {
    Router addLocal(Predicate<String> predicate, Route route);
    Router setGlobal(Predicate<String> predicate, Route route);
    Router setDefaultLocal(Route route);
    State process(String command, Session session);
}
