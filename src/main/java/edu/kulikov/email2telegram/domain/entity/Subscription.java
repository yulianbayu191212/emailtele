package edu.kulikov.email2telegram.domain.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 04.09.2016
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue
    private Long subscriptionId;
    @NonNull
    private Long telegramChatId;
    @NonNull
    private String chatName;
    @NotNull
    private boolean active = true;
    @NonNull
    @NotNull
    private boolean owner;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        return subscriptionId != null ? subscriptionId.equals(that.subscriptionId) : that.subscriptionId == null;
    }

    @Override
    public int hashCode() {
        return subscriptionId != null ? subscriptionId.hashCode() : 0;
    }
}
