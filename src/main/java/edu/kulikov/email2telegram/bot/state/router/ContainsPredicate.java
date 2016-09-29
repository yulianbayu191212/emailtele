package edu.kulikov.email2telegram.bot.state.router;

import java.util.function.Predicate;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 25.09.2016
 */
public class ContainsPredicate implements Predicate<String> {
    private String substr;

    public ContainsPredicate(String substr) {
        this.substr = substr;
    }

    @Override
    public boolean test(String s) {
        return s.contains(substr);
    }

    public static ContainsPredicate contains(String substr) {
        return new ContainsPredicate(substr);
    }
}
