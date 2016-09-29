package edu.kulikov.email2telegram.domain.repository;

import edu.kulikov.email2telegram.domain.entity.MailProviderType;
import edu.kulikov.email2telegram.domain.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 17.09.2016
 */
@Repository
public interface ProviderRepository extends JpaRepository<Provider, MailProviderType> {
}
