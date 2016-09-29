package edu.kulikov.email2telegram.domain.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 04.09.2016
 */
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Entity
public class TelegramUser {
    @Id
    @NonNull
    private Integer telegramUserId;
    @NonNull
    private String login;
    @NonNull
    private String firstName;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TelegramUser that = (TelegramUser) o;

        return telegramUserId != null ? telegramUserId.equals(that.telegramUserId) : that.telegramUserId == null;

    }

    @Override
    public int hashCode() {
        return telegramUserId != null ? telegramUserId.hashCode() : 0;
    }
}
