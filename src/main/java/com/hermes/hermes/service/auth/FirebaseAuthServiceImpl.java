package com.hermes.hermes.service.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthServiceImpl implements FirebaseAuthService {

    @Override
    public UserRecord createUser(UserRecord.CreateRequest request) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().createUser(request);
    }

    @Override
    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    @Override
    public void revokeRefreshTokens(String uid) throws FirebaseAuthException {
        FirebaseAuth.getInstance().revokeRefreshTokens(uid);
    }
}