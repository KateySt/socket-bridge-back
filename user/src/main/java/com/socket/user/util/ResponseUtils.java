package com.socket.user.util;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

public class ResponseUtils {

    public static ResponseEntity<String> internalError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }

    public static ResponseEntity<String> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    public static ResponseEntity<String> badRequest(String message) {
        return ResponseEntity.badRequest().body(message);
    }

    public static ResponseEntity<?> tryOrError(Supplier<ResponseEntity<?>> action) {
        try {
            return action.get();
        } catch (Exception e) {
            e.printStackTrace();
            return internalError("Unexpected server error");
        }
    }
}