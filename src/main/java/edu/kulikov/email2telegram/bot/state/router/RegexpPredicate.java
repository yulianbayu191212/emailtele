package edu.kulikov.email2telegram.bot.state.router;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 25.09.2016
 */
public class RegexpPredicate implements Predicate<String> {
    private Pattern pattern;


    public RegexpPredicate(String regexp) {
        pattern = Pattern.compile(regexp);
    }

    @Override
    public boolean test(String s) {
        return pattern.matcher(s).matches();
    }

    public static RegexpPredicate regExp(String regexp) {
        return new RegexpPredicate(regexp);
    }
}
