package wepin.store.service;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Message.Builder;
import com.google.firebase.messaging.Notification;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.FcmSendDto.PutDataDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;

    public void sendMessage(String title, String body, String token, List<PutDataDto> putDataDtoList) throws FirebaseMessagingException {

        Message.Builder messageBuilder = Message.builder()
                                                .setToken(token)
                                                .setNotification(Notification.builder()
                                                                             .setTitle(title)
                                                                             .setBody(body)
                                                                             .build());

        send(putDataDtoList, messageBuilder);
    }

    public void sendMessage(String body, String token, List<PutDataDto> putDataDtoList) throws FirebaseMessagingException {

        Message.Builder messageBuilder = Message.builder()
                                                .setToken(token)
                                                .setNotification(Notification.builder()
                                                                             .setBody(body)
                                                                             .build());

        send(putDataDtoList, messageBuilder);
    }

    public void sendMessage(String body, String token) throws FirebaseMessagingException {

        Message.Builder messageBuilder = Message.builder()
                                                .setToken(token)
                                                .setNotification(Notification.builder()
                                                                             .setBody(body)
                                                                             .build());
        send(new ArrayList<>(), messageBuilder);
    }

    private void send(List<PutDataDto> putDataDtoList, Builder messageBuilder) throws FirebaseMessagingException {
        if (!putDataDtoList.isEmpty()) {
            for (PutDataDto data : putDataDtoList) {
                messageBuilder.putData(data.getKey(), data.getValue());
            }
        }

        Aps aps = Aps.builder()
                     .setSound("default")
                     .setContentAvailable(true)
                     .build();

        ApnsConfig apnsConfig = ApnsConfig.builder()
                                          .setAps(aps)
                                          .build();

        messageBuilder.setApnsConfig(apnsConfig);

        try {
            Message message = messageBuilder.build();
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            //            log.error("알림 메세지 전송 에러 : ", e);
            throw e;
        }
    }
}
