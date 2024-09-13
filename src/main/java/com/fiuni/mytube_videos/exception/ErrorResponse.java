package com.fiuni.mytube_videos.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private String details;
    private Date timestamp;

    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
        this.timestamp = new Date();
    }
}
