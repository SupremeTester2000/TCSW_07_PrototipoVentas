
package com.example.adapters.inbound.rest.exception;

public final class ApiError {

    private final String title;
    private final int status;
    private final String detail;

    public ApiError(String title, int status, String detail) {
        this.title = title;
        this.status = status;
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }
}
