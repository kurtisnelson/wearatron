package com.thisisnotajoke.lockitron;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

public class LockitronOAuthApi extends DefaultApi20 {
    private static FirebaseRemoteConfig sRemoteConfig = FirebaseRemoteConfig.getInstance();

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return sRemoteConfig.getString("lockitron_access_token_endpoint");
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config)
    {
        Preconditions.checkValidUrl(config.getCallback(),
                "Must provide a valid url as callback. Lockitron does not support OOB");
        return String.format(sRemoteConfig.getString("lockitron_authorization_url"),
                config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor()
    {
        return new JsonTokenExtractor();
    }
}
