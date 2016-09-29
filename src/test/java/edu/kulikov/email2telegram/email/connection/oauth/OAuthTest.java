package edu.kulikov.email2telegram.email.connection.oauth;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.token.OAuthToken;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 24.09.2016
 */
public class OAuthTest {
    @Test
    @Ignore
    public void test() throws OAuthProblemException, OAuthSystemException, IOException {
        GMailOAuth2Provider GMailOAuth2Provider = new GMailOAuth2Provider();
        GMailOAuth2Provider.setClientId("206591512769-0s4n32qetchtruveufija1thjkl8ocka.apps.googleusercontent.com");
        GMailOAuth2Provider.setClientSecret("WGoWv6kahQLZV40GUuXhZazE");
        GMailOAuth2Provider.setRedirectUrl("http_server://localhost");


        //in web application you make redirection to uri:
        System.out.println("Visit: " + GMailOAuth2Provider.getAuthLink("sdsd") + "\nand grant permission");

        System.out.print("Now enter the OAuth code you have received in redirect uri ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();

        OAuthToken token = GMailOAuth2Provider.getAccessTokenByCode(code);
        System.out.println(
                "Access Token: " + token.getAccessToken() + ", Expires in: " + token
                        .getExpiresIn());

        String emailByToken = GMailOAuth2Provider.getLoginByAccessToken(token.getAccessToken());
        System.out.println("MailboxAccount by token: " + emailByToken);


        System.out.println("Refresh token: new access token:" + GMailOAuth2Provider.refreshAccessToken("1/SoBm2IoAcgmRvMPj4rLgCwTJ93Q0Fs-JssfrciuyRKc").getAccessToken());
    }

}