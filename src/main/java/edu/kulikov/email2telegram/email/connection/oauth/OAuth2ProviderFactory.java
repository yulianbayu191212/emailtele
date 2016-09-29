package edu.kulikov.email2telegram.email.connection.oauth;

import edu.kulikov.email2telegram.domain.entity.MailProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static edu.kulikov.email2telegram.domain.entity.MailProviderType.GMAIL;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 11.09.2016
 */
@Component
public class OAuth2ProviderFactory {
    private Map<MailProviderType, OAuth2Provider> map;
    private GMailOAuth2Provider gmailProvider;

    public OAuth2ProviderFactory() {
        map = new HashMap<>();
    }

    public OAuth2Provider get(MailProviderType type) {
        return map.get(type);
    }

    public void add(MailProviderType type, OAuth2Provider provider) {
        map.put(type, provider);
    }

    @PostConstruct
    public void fillInitData() {
        add(GMAIL, gmailProvider);
    }

    @Autowired
    public void setGmailProvider(GMailOAuth2Provider gmailProvider) {
        this.gmailProvider = gmailProvider;
    }
}
