package edu.kulikov.email2telegram.domain.service;

import edu.kulikov.email2telegram.domain.entity.MailProviderType;
import edu.kulikov.email2telegram.domain.entity.Provider;
import edu.kulikov.email2telegram.domain.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

import static edu.kulikov.email2telegram.domain.entity.MailProviderType.GMAIL;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 17.09.2016
 */
@Service
public class ProviderService {
    private ProviderRepository providerRepository;

    @Autowired
    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @PostConstruct
    public void fillInitialData() {
        save(new Provider(GMAIL, "imap.gmail.com", 993, true));
    }

    public List<Provider> findAll() {
        return providerRepository.findAll();
    }

    @Transactional
    public Provider findByType(MailProviderType type) {
        return providerRepository.findOne(type);
    }

    public Provider save(Provider provider) {
        return providerRepository.save(provider);
    }
}
