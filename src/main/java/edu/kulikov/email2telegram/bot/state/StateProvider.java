package edu.kulikov.email2telegram.bot.state;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 07.09.2016
 */
@Component
public class StateProvider implements ApplicationContextAware {
    private static StateProvider instance;
    private ApplicationContext applicationContext;

    private MainMenuState mainMenuState;
    private AddMailbox_AccountTypeState addMailbox_accountTypeState;
    private AddMailbox_TokenState addMailbox_tokenState;
    private MyMailboxes_MenuState mailboxes_menuState;
    private MyMailboxes_MailboxState mailboxes_MailboxState;
    private Subscriptions_MainMenuState subscriptions_mainMenuState;
    private Subscriptions_AddNewState subscriptions_addNewState;
    private Subscriptions_EditState subscriptions_editState;


    public static StateProvider getStates() {
        return instance;
    }

    public MainMenuState getMainMenuState() {
        return mainMenuState;
    }

    @Autowired
    public void setMainMenuState(MainMenuState mainMenuState) {
        this.mainMenuState = mainMenuState;
    }

    public AddMailbox_AccountTypeState getAddMailBox_AccountTypeState() {
        return addMailbox_accountTypeState;
    }

    @Autowired
    public void setAddMailbox_accountTypeState(AddMailbox_AccountTypeState addMailbox_accountTypeState) {
        this.addMailbox_accountTypeState = addMailbox_accountTypeState;
    }


    public AddMailbox_TokenState getAddMailbox_tokenState() {
        return addMailbox_tokenState;
    }

    @Autowired
    public void setAddMailbox_tokenState(AddMailbox_TokenState addMailbox_tokenState) {
        this.addMailbox_tokenState = addMailbox_tokenState;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        instance = this;
    }

    public MyMailboxes_MenuState getMailboxes_menuState() {
        return mailboxes_menuState;
    }

    @Autowired
    public void setMailboxes_menuState(MyMailboxes_MenuState mailboxes_menuState) {
        this.mailboxes_menuState = mailboxes_menuState;
    }

    public MyMailboxes_MailboxState getMailboxes_MailboxState() {
        return mailboxes_MailboxState;
    }

    @Autowired
    public void setMailboxes_MailboxState(MyMailboxes_MailboxState mailboxes_MailboxState) {
        this.mailboxes_MailboxState = mailboxes_MailboxState;
    }

    public Subscriptions_MainMenuState getSubscriptions_mainMenuState() {
        return subscriptions_mainMenuState;
    }

    @Autowired
    public void setSubscriptions_mainMenuState(Subscriptions_MainMenuState subscriptions_mainMenuState) {
        this.subscriptions_mainMenuState = subscriptions_mainMenuState;
    }

    public Subscriptions_AddNewState getSubscriptions_addNewState() {
        return subscriptions_addNewState;
    }

    @Autowired
    public void setSubscriptions_addNewState(Subscriptions_AddNewState subscriptions_addNewState) {
        this.subscriptions_addNewState = subscriptions_addNewState;
    }

    public Subscriptions_EditState getSubscriptions_editState() {
        return subscriptions_editState;
    }

    @Autowired
    public void setSubscriptions_editState(Subscriptions_EditState subscriptions_editState) {
        this.subscriptions_editState = subscriptions_editState;
    }
}
