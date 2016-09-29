package edu.kulikov.email2telegram.bot;

import edu.kulikov.email2telegram.bot.state.State;
import edu.kulikov.email2telegram.bot.state.StatePhase;
import edu.kulikov.email2telegram.bot.state.StateProvider;
import edu.kulikov.email2telegram.bot.state.session.Session;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 09.09.2016
 */
public class UserContext {
    private State state;
    private StatePhase statePhase;
    private Session session;

    public UserContext() {
        statePhase = StatePhase.REQUEST;
        state = StateProvider.getStates().getMainMenuState();
        session = new Session();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public StatePhase getStatePhase() {
        return statePhase;
    }

    public void setStatePhase(StatePhase statePhase) {
        this.statePhase = statePhase;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
