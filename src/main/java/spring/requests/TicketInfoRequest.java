package spring.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import spring.entity.SeasonTicket;

@Getter
@Setter
@AllArgsConstructor
public class TicketInfoRequest
{
    private String ticketType;
    private Integer availableClasses;
}
