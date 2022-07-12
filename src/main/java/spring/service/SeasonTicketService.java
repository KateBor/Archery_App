package spring.service;

import spring.entity.SeasonTicket;

import java.time.LocalTime;
import java.util.List;

public interface SeasonTicketService
{
    List<SeasonTicket> findTickets();
    List<SeasonTicket> areForSale();

    List<SeasonTicket> areNotForSale();
    void addSeasonTicket(SeasonTicket seasonTicket);
    void changeIfIsForSale(SeasonTicket seasonTicket);
    boolean removeTicket(SeasonTicket seasonTicket);
}
