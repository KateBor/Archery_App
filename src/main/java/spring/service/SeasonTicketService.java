package spring.service;

import spring.entity.SeasonTicket;

import java.sql.Time;
import java.util.List;

public interface SeasonTicketService
{
    List<SeasonTicket> areForSale();
    List<SeasonTicket> areNotForSale();
    Time getTimeDuration(String ticketType);
    SeasonTicket addSeasonTicket(SeasonTicket seasonTicket);
    void changeIfIsForSale(SeasonTicket seasonTicket);
}
