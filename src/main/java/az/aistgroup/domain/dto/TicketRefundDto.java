package az.aistgroup.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TicketRefundDto {
    @NotNull(message = "{field.notNull}")
    @Positive(message = "{field.positive}")
    private Long ticketId;

    @NotBlank(message = "{field.notBlank}")
    private String username;

    public TicketRefundDto() {}

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
