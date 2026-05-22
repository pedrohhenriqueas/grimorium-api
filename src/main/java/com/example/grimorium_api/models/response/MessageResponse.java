package com.example.grimorium_api.models.response;

public class MessageResponse {
    public static final String SUCCESS = "Success.";
    public static final String ERROR = "Error processing request.";
    public static final String UNAUTHORIZED = "Invalid or missing Authorization header";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred.";
    
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageResponse(String message) {
        this.message = message;
    }
}
