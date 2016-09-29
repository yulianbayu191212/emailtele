package edu.kulikov.email2telegram.email.connection.oauth;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.token.OAuthToken;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 05.09.2016
 */
public interface OAuth2Provider {
    String getAuthLink(String state) throws OAuthSystemException;
    OAuthToken getAccessTokenByCode(String code) throws OAuthSystemException, OAuthProblemException;
    String getLoginByAccessToken(String token) throws OAuthSystemException, OAuthProblemException;
    OAuthToken refreshAccessToken(String refreshToken) throws OAuthSystemException, OAuthProblemException;
}
