package edu.kulikov.email2telegram.spring;

import edu.kulikov.email2telegram.domain.entity.Provider;
import edu.kulikov.email2telegram.domain.service.ProviderService;
import edu.kulikov.email2telegram.email.connection.oauth.GMailOAuth2Provider;
import edu.kulikov.email2telegram.email.connection.oauth.OAuth2ProviderFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static edu.kulikov.email2telegram.domain.entity.MailProviderType.GMAIL;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 24.09.2016
 */
@Configuration
public class ConfigInitialData implements ApplicationContextAware {
    @Autowired
    private ProviderService providerService;
    @Autowired
    private OAuth2ProviderFactory mailProviderFactory;
    private ApplicationContext context;

    @PostConstruct
    void fillInitialData() {
        //Add Gmail provider
        providerService.save(new Provider(GMAIL, "imap.gmail.com", 993, true));
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
