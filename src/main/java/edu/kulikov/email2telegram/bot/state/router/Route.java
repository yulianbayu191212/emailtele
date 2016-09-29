package edu.kulikov.email2telegram.bot.state.router;

import edu.kulikov.email2telegram.bot.state.State;
import edu.kulikov.email2telegram.bot.state.session.Session;

import java.util.function.BiFunction;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 09.09.2016
 */
public interface Route extends BiFunction<String,Session,State> {
}
