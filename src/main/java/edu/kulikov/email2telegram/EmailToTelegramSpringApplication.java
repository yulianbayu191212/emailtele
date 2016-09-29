package edu.kulikov.email2telegram;

import edu.kulikov.email2telegram.bot.EmailBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.BotSession;

import javax.annotation.PreDestroy;

@SpringBootApplication
@Slf4j
public class EmailToTelegramSpringApplication extends SpringBootServletInitializer {


    private BotSession botSession;

    public static void main(String[] args) {
        SpringApplication.run(EmailToTelegramSpringApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EmailToTelegramSpringApplication.class);
    }

    @Bean
    public CommandLineRunner startBot(EmailBot emailBot) {
        return (args) -> {
//            MailboxAccount withSubscriptions = mailboxAccountService.findWithSubscriptions(1L);
//            List<Subscription> subscriptions = withSubscriptions.getSubscriptions();
//            subscriptions.add(new Subscription(145L,"Chat name"));
//            mailboxAccountService.save(withSubscriptions);
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                botSession = telegramBotsApi.registerBot(emailBot);
            } catch (TelegramApiException e) {
                log.error("[TelegramBot] connection error", e);
            }
        };
    }

    @PreDestroy
    public void shutdown() {
        if (botSession != null) {
            botSession.close();
        }
    }


}
