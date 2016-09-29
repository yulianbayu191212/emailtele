package edu.kulikov.email2telegram.domain.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 18.09.2016
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
public class MergeJpaRepository<T, ID extends Serializable> extends
        SimpleJpaRepository<T, ID> {

    private EntityManager em;

    public MergeJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.em = entityManager;
    }

    public MergeJpaRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.em = entityManager;
    }

    //merge in any case to save/update nested objects automatically
    @Override
    @Transactional
    public <S extends T> S save(S s) {
        return em.merge(s);
    }
}
