package com.nuclominus.offlinetwitterclient.Utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.persistence.SerializationStrategy;

public class Serializer implements SerializationStrategy<TwitterSession> {

    private final Gson gson;

    public Serializer() {
        this.gson = new Gson();
    }

    @Override
    public String serialize(TwitterSession session) {
        if (session != null && session.getAuthToken() != null) {
            try {
                return gson.toJson(session);
            } catch (Exception e) {
                Fabric.getLogger().d(TwitterCore.TAG, e.getMessage());
            }
        }
        return "";
    }

    @Override
    public TwitterSession deserialize(String serializedSession) {
        if (!TextUtils.isEmpty(serializedSession)) {
            try {
                return gson.fromJson(serializedSession, TwitterSession.class);
            } catch (Exception e) {
                Fabric.getLogger().d(TwitterCore.TAG, e.getMessage());
            }
        }
        return null;
    }
}
