package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TicketWrapper {
    @JsonProperty("tickets")
    private List<Ticket> ticketList;
}
