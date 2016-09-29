package edu.kulikov.email2telegram.domain.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.time.Instant;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 19.09.2016
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class AccountToken {
    private String accessToken;
    private String refreshToken;
    private Long tokenLifespan;
    private Instant tokenEndTime;
}
