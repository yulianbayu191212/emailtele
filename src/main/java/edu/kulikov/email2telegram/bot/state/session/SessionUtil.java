package edu.kulikov.email2telegram.bot.state.session;

import edu.kulikov.email2telegram.bot.state.Constants;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 10.09.2016
 */
public class SessionUtil {

    public static void addFlash(Session session, String key, Object value) {
        session.addParam(key, value, true);
    }

    public static void setUnknownValue(Session session, String value) {
        addFlash(session, Constants.UNKNOWN_COMMAND_KEY, value);
    }

    public static String getUnknownValue(Session session) {
        return (String) session.getParam(Constants.UNKNOWN_COMMAND_KEY);
    }


}
