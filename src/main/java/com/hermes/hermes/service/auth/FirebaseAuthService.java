package com.hermes.hermes.service.auth;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

public interface FirebaseAuthService {
    UserRecord createUser(UserRecord.CreateRequest request);
    FirebaseToken verifyIdToken(String idToken);
    void revokeRefreshTokens(String uid);
}