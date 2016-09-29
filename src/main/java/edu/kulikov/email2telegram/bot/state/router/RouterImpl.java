package edu.kulikov.email2telegram.bot.state.router;

import edu.kulikov.email2telegram.bot.state.State;
import edu.kulikov.email2telegram.bot.state.session.Session;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.apache.commons.lang3.tuple.Pair.of;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 09.09.2016
 */
public class RouterImpl implements Router {
    private List<Pair<Predicate<String>, Route>> localRoutes;
    private Pair<Predicate<String>, Route> globalRoute;
    private Route localRouteDefault;

    public RouterImpl() {
        localRoutes = new ArrayList<>();
    }

    @Override
    public State process(String command, Session session) {
        //try local routers
        for (Pair<Predicate<String>, Route> routeData : localRoutes) {
            if (routeData.getKey().test(command)) {
                State state = routeData.getValue().apply(command, session);
                if (state != null) return state;
            }
        }
        //try global route
        if (globalRoute != null) {
            if (globalRoute.getKey().test(command)) {
                State state = globalRoute.getValue().apply(command, session);
                if (state != null) return state;
            }
        }
        if (localRouteDefault != null) {
            //use default one
            return localRouteDefault.apply(command, session);
        }
        return null;
    }


    @Override
    public Router addLocal(Predicate<String> predicate, Route route) {
        localRoutes.add(of(predicate, route));
        return this;
    }

    @Override
    public Router setGlobal(Predicate<String> predicate, Route route) {
        globalRoute = of(predicate, route);
        return this;
    }

    @Override
    public Router setDefaultLocal(Route route) {
        localRouteDefault = route;
        return this;
    }

}
