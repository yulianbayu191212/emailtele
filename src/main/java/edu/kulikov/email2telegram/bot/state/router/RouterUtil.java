package edu.kulikov.email2telegram.bot.state.router;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 29.09.2016
 */
public class RouterUtil {
    public static String getCommandPayload(String command, String input) {
        Pattern pattern = Pattern.compile(command + " (.*)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }
}
