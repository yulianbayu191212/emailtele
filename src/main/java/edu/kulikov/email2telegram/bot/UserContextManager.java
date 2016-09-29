package edu.kulikov.email2telegram.bot;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 07.09.2016
 */
@Component
public class UserContextManager {
    private Map<Integer, UserContext> userContextMap;

    public UserContextManager() {
        userContextMap = new HashMap<>();
    }

    public UserContext get(Integer userId) {
        UserContext userContext = userContextMap.get(userId);
        if (userContext == null) {
            userContext = new UserContext();
            userContextMap.put(userId, userContext);
        }
        return userContext;
    }
}
