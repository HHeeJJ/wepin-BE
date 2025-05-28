package wepin.store.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FCMConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        FirebaseApp       firebaseApp     = null;
        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

        if (!firebaseAppList.isEmpty()) {
            firebaseApp = firebaseAppList.stream()
                                         .filter(app -> FirebaseApp.DEFAULT_APP_NAME.equals(app.getName()))
                                         .findFirst()
                                         .orElse(null);
        }

        if (firebaseApp == null) {
            try (InputStream refreshToken = new ClassPathResource("firebase/wepin-a01b8-firebase-adminsdk-b3c6d-d58ad0514a.json").getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                                                         .setCredentials(GoogleCredentials.fromStream(refreshToken))
                                                         .build();
                firebaseApp = FirebaseApp.initializeApp(options);
            }
        }

        return FirebaseMessaging.getInstance(firebaseApp);
    }
}

