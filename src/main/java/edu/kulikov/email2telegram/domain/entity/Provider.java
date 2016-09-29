package edu.kulikov.email2telegram.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import static javax.persistence.EnumType.STRING;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 12.09.2016
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Provider {
    @Id
    @Enumerated(STRING)
    private MailProviderType type;
    private String host;
    private Integer port;
    private Boolean ssl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Provider provider = (Provider) o;

        return type == provider.type;

    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
