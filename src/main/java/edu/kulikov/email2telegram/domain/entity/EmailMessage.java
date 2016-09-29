package edu.kulikov.email2telegram.domain.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 21.09.2016
 */
@Getter
@Setter
@RequiredArgsConstructor
public class EmailMessage {
    @NonNull
    private String from;
    @NonNull
    private String subject;
    @NonNull
    private String content;

    @Override
    public String toString() {
        return "EmailMessage{" +
                "from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
