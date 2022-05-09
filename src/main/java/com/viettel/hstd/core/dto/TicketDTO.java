package com.viettel.hstd.core.dto;

import javax.validation.constraints.NotBlank;

public class TicketDTO {
    @NotBlank(message = "Invalid Ticket!")
    public String ticket;
}
