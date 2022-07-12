package spring.service;

import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.entity.SeasonTicket;
import spring.exception.SeasonTicketNotFoundException;
import spring.repositories.PurchaseHistoryRepository;
import spring.repositories.SeasonTicketRepository;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class SeasonTicketServiceImpl implements SeasonTicketService {
    @Autowired
    private SeasonTicketRepository seasonTicketRepository;
    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    public List<SeasonTicket> findTickets()
    {
        return seasonTicketRepository.findAll();
    }
    public List<SeasonTicket> areForSale()
    {
        return seasonTicketRepository.findByIsForSale(true);
    }

    public List<SeasonTicket> areNotForSale()
    {
        return seasonTicketRepository.findByIsForSale(false);
    }
    public void addSeasonTicket(SeasonTicket seasonTicket)
    {
        seasonTicketRepository.save(seasonTicket);
    }
    public void changeIfIsForSale(SeasonTicket seasonTicket)
    {
        seasonTicketRepository.changeIfIsForSale(seasonTicket.getTicketType(), !seasonTicket.getIsForSale());
    }
    public boolean removeTicket(SeasonTicket seasonTicket)
    {
        if (purchaseHistoryRepository.findBySeasonTicket(seasonTicket))
        {
            return false;
        }
        seasonTicketRepository.removeByTicketType(seasonTicket.getTicketType());
        return true;
    }
}
