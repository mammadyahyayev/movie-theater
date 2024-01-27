package az.aistgroup.exception;

public enum ErrorResponseCode {
    RESOURCE_NOT_FOUND("resource.not.found"),
    RESOURCE_ALREADY_EXIST("resource.exist"),
    BAD_CREDENTIALS("bad.credentials"),
    INTERNAL_SERVER_ERROR("internal.server.error"),
    ACCESS_DENIED("access.denied"),
    INVALID_REQUEST("invalid.request"),
    UNAUTHORIZED("unauthorized.request"),
    CAPACITY_EXCEEDED("capacity.exceeded"),
    SEAT_BOOKED("seat.booked"),
    INSUFFICIENT_FUNDS("insufficient.funds"),
    NO_TICKETS_AVAILABLE("zero.tickets.left");

    private final String code;

    ErrorResponseCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
