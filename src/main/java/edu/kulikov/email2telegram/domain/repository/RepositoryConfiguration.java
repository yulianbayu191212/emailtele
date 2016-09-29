package edu.kulikov.email2telegram.domain.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 18.09.2016
 */
@Configuration
@EnableJpaRepositories(value = "edu.kulikov.email2telegram.domain.repository",
        repositoryBaseClass = MergeJpaRepository.class)
public class RepositoryConfiguration {
}
