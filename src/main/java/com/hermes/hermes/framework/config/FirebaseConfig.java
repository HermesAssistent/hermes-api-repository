    package com.hermes.hermes.framework.config;

    import com.google.auth.oauth2.GoogleCredentials;
    import com.google.firebase.FirebaseApp;
    import com.google.firebase.FirebaseOptions;
    import com.hermes.hermes.framework.exception.AuthenticationException;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpStatus;

    import javax.annotation.PostConstruct;
    import java.io.FileInputStream;
    import java.io.IOException;

    @Configuration
    public class FirebaseConfig {

        @Value("${firebase.service-account.path}")
        private String serviceAccountPath;

        @PostConstruct
        public void initializeFirebase() throws IOException {
            try (FileInputStream serviceAccount = new FileInputStream(serviceAccountPath)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    System.out.println("Firebase inicializado com sucesso!");
                }
            } catch (Exception ex) {
                throw new AuthenticationException(HttpStatus.UNAUTHORIZED,"Sessão expirada!");
            }
        }
    }