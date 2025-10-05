package com.hermes.hermes.service.auth;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

public interface FirebaseAuthService {
    UserRecord createUser(UserRecord.CreateRequest request) throws FirebaseAuthException;
    FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException;
    void revokeRefreshTokens(String uid) throws FirebaseAuthException;
}