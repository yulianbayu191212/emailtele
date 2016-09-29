package edu.kulikov.email2telegram.domain.repository;

import edu.kulikov.email2telegram.domain.entity.MailboxAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 04.09.2016
 */
@Repository
public interface MailboxAccountRepository extends JpaRepository<MailboxAccount, Long> {
    List<MailboxAccount> findByOwnerTelegramUserId(Integer telegramUserId);
    MailboxAccount findByOwnerTelegramUserIdAndMailbox(Integer telegramUserId, String mailbox);
}
