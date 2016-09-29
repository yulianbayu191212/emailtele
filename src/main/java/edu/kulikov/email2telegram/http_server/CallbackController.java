package edu.kulikov.email2telegram.http_server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.Cache;
import edu.kulikov.email2telegram.cache.InternalStateCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 10.09.2016
 */
@Controller
public class CallbackController {
    private Cache<String, Object> tokenCache;
    @Value("${email2telegram.bot.name}")
    private String botName;

    @RequestMapping("/callback")
    public String oAuthCallback(@RequestParam String code,
                                @RequestParam String state,
                                Model model) throws JsonProcessingException {
        if (tokenCache.getIfPresent(state) == null) {
            //TODO: show page 'Token Not found, request new'
        }
        tokenCache.put(state, code);
        String link = "https://telegram.me/" + botName + "?start=" + state;
        model.addAttribute("link", link);
        return "callback_success";
    }

    @Autowired
    @InternalStateCache
    public void setTokenCache(Cache<String, Object> tokenCache) {
        this.tokenCache = tokenCache;
    }

}
