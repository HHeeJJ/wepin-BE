package wepin.store.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ErrorUtill {
    public static void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 Unauthorized
        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = "{\"error\":\"" + message + "\"}";
        response.getWriter()
                .write(jsonResponse);
    }

}