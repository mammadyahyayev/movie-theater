package az.aistgroup.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public final class TicketRequestDto {

    @Positive
    @NotNull
    private Long sessionId;

    @Positive
    @NotNull
    private Long seatId;

    @Positive
    @NotNull
    private String username;

    public TicketRequestDto() {}

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
