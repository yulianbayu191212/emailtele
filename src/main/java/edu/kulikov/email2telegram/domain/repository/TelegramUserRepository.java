package edu.kulikov.email2telegram.domain.repository;

import edu.kulikov.email2telegram.domain.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 11.09.2016
 */
@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Integer> {
}
