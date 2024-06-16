package com.midel.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestResponse extends CustomResponse {

    private final Object data;

    public RestResponse(HttpStatus status, Object data) {
        super(status);

        this.data = data;
    }

}
