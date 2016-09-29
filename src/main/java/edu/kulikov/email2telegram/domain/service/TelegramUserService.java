package edu.kulikov.email2telegram.domain.service;

import edu.kulikov.email2telegram.domain.entity.TelegramUser;
import edu.kulikov.email2telegram.domain.repository.TelegramUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 11.09.2016
 */
@Service
public class TelegramUserService {
    private final TelegramUserRepository userRepository;

    @Autowired
    public TelegramUserService(TelegramUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveIfNotPresent(TelegramUser user) {
        if (!userRepository.exists(user.getTelegramUserId())) {
            userRepository.save(user);
        }
    }

}
