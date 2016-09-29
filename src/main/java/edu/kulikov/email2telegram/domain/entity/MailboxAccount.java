package edu.kulikov.email2telegram.domain.entity;

import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.annotations.CascadeType.*;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 04.09.2016
 */
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MailboxAccount {
    @Id
    @GeneratedValue
    private Long accountId;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            joinColumns = {@JoinColumn(name = "account_id")},
            inverseJoinColumns = {@JoinColumn(name = "subscription_id")})
    @Cascade({SAVE_UPDATE, MERGE, REFRESH, PERSIST, DELETE})
    private List<Subscription> subscriptions = new ArrayList<>();
    private String mailbox;
    @Embedded
    private AccountToken accountToken;
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({SAVE_UPDATE, MERGE, REFRESH, PERSIST})
    private Provider provider;
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({SAVE_UPDATE, MERGE, REFRESH, PERSIST})
    private TelegramUser owner;

    public MailboxAccount(String mailbox, AccountToken accountToken,
                          Provider provider, TelegramUser owner) {
        this.mailbox = mailbox;
        this.accountToken = accountToken;
        this.provider = provider;
        this.owner = owner;
    }

    public MailboxAccount(Provider provider, TelegramUser owner) {
        this.provider = provider;
        this.owner = owner;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MailboxAccount mailboxAccount = (MailboxAccount) o;

        return accountId != null ? accountId.equals(mailboxAccount.accountId) : mailboxAccount.accountId == null;
    }

    @Override
    public int hashCode() {
        return accountId != null ? accountId.hashCode() : 0;
    }
}
