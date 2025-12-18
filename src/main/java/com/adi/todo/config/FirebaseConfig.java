package com.adi.todo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_PROJECT_ID}")
    private String projectId;

    @Value("${FIREBASE_PRIVATE_KEY_ID}")
    private String privateKeyId;

    @Value("${FIREBASE_PRIVATE_KEY}")
    private String privateKey;

    @Value("${FIREBASE_CLIENT_EMAIL}")
    private String clientEmail;

    @PostConstruct
    public void initialize() {
        try {
            String formattedPrivateKey = privateKey
                    .replace("\\n", "\n")
                    .replace("\"", "\\\""); // Escape any quotes in the private key
            String serviceAccountJson = String.format("""
                    {
                      "type": "service_account",
                      "project_id": "%s",
                      "private_key_id": "%s",
                      "private_key": "%s",
                      "client_email": "%s",
                      "client_id": "",
                      "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                      "token_uri": "https://oauth2.googleapis.com/token",
                      "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
                      "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/%s"
                    }
                    """,
                    projectId,
                    privateKeyId,
                    formattedPrivateKey,
                    clientEmail,
                    clientEmail);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ByteArrayInputStream(serviceAccountJson.getBytes())))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}