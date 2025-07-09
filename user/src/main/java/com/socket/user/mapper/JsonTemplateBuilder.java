package com.socket.user.mapper;

import com.socket.user.dto.RegisterRequest;
import com.socket.user.dto.UpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class JsonTemplateBuilder {

    public String buildCreateUserJson(RegisterRequest req) {
        return String.format("""
            {
              "username": "%s",
              "enabled": true,
              "firstName": "%s",
              "lastName": "%s",
              "email": "%s",
              "emailVerified": true,
              "credentials": [{
                "type": "password",
                "value": "%s",
                "temporary": false
              }]
            }
            """, req.username(), req.firstName(), req.lastName(), req.email(), req.password());
    }

    public String buildUpdateUserJson(UpdateRequest update) {
        return String.format("""
            {
              "firstName": "%s",
              "lastName": "%s"
            }
            """, update.firstName(), update.lastName());
    }
}