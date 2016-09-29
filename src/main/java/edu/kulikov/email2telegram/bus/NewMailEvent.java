package edu.kulikov.email2telegram.bus;

import edu.kulikov.email2telegram.domain.entity.EmailMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 21.09.2016
 */
@Setter
@Getter
@AllArgsConstructor
public class NewMailEvent {
    private Long accountId;
    private EmailMessage message;
}
