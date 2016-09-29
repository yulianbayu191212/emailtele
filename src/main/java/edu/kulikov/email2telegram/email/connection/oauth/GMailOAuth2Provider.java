package edu.kulikov.email2telegram.email.connection.oauth;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.token.OAuthToken;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.apache.oltu.oauth2.common.OAuth.HttpMethod.GET;
import static org.apache.oltu.oauth2.common.OAuth.HttpMethod.POST;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 06.09.2016
 */
@Component
public class GMailOAuth2Provider implements OAuth2Provider {
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private OAuthClient oAuthClient;

    public GMailOAuth2Provider() {
        oAuthClient = new OAuthClient(new URLConnectionClient());
    }

    @Override
    public String getAuthLink(String state) throws OAuthSystemException {
        return OAuthClientRequest
                .authorizationProvider(OAuthProviderType.GOOGLE)
                .setClientId(clientId)
                .setScope("https://mail.google.com/ email")
                .setState(state)
                .setRedirectURI(redirectUrl)
                .setResponseType("code")
                .buildQueryMessage().getLocationUri();
    }

    @Override
    public OAuthToken getAccessTokenByCode(String code) throws OAuthSystemException, OAuthProblemException {
        OAuthClientRequest request = OAuthClientRequest
                .tokenProvider(OAuthProviderType.GOOGLE)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI(redirectUrl)
                .setCode(code)
                .buildBodyMessage();
        OAuthJSONAccessTokenResponse response = oAuthClient.accessToken(request);
        return response.getOAuthToken();
    }

    @Override
    public String getLoginByAccessToken(String accessToken) throws OAuthSystemException, OAuthProblemException {
        OAuthClientRequest request = new OAuthBearerClientRequest("https://www.googleapis.com/oauth2/v1/userinfo")
                .setAccessToken(accessToken)
                .buildQueryMessage();
        OAuthResourceResponse resource = oAuthClient.resource(request, GET, OAuthResourceResponse.class);
        return new JSONObject(resource.getBody()).getString("email");
    }

    @Override
    public OAuthToken refreshAccessToken(String refreshToken) throws OAuthSystemException, OAuthProblemException {
        OAuthClientRequest oAuthClientRequest = new OAuthClientRequest.TokenRequestBuilder("https://www.googleapis.com/oauth2/v4/token").
                setRefreshToken(refreshToken).
                setClientId(clientId).
                setClientSecret(clientSecret).
                setGrantType(GrantType.REFRESH_TOKEN).buildBodyMessage();
        OAuthJSONAccessTokenResponse resource = oAuthClient.resource(oAuthClientRequest, POST, OAuthJSONAccessTokenResponse.class);
        return resource.getOAuthToken();
    }


    @Value("${oauth2.google.client-id}")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Value("${oauth2.google.client-secret}")
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Value("${oauth2.google.redirect-url}")
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
