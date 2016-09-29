package edu.kulikov.email2telegram.bot.state.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 08.09.2016
 */
public class Session {
    private Map<String, Object> params;
    private Set<String> oneTimeParams;


    public Session() {
        params = new HashMap<>();
        oneTimeParams = new HashSet<>();
    }

    public void addParam(String key, Object value, boolean oneTime) {
        params.put(key, value);
        if (oneTime) {
            oneTimeParams.add(key);
        }
    }

    public void clearParam(String key) {
        params.remove(key);
    }

    public Object getParam(String key) {
        Object value = params.get(key);
        if (oneTimeParams.contains(key)) {
            params.remove(key);
            oneTimeParams.remove(key);
        }
        return value;
    }

}
