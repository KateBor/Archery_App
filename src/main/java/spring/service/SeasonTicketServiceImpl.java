package spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.entity.SeasonTicket;
import spring.exception.SeasonTicketNotFoundException;
import spring.repositories.PurchaseHistoryRepository;
import spring.repositories.SeasonTicketRepository;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Service
public class SeasonTicketServiceImpl implements SeasonTicketService {
    private SeasonTicketRepository seasonTicketRepository;
    private PurchaseHistoryRepository purchaseHistoryRepository;

    public List<SeasonTicket> areForSale()
    {
        return seasonTicketRepository.findByIsForSale(true);
    }

    public List<SeasonTicket> areNotForSale()
    {
        return seasonTicketRepository.findByIsForSale(false);
    }

    public Time getTimeDuration(String ticketType)
    {
        Optional<SeasonTicket> optionalSeasonTicket = seasonTicketRepository.findByTicketType(ticketType);
        if (optionalSeasonTicket.isPresent())
        {
            return optionalSeasonTicket.get().getTimeDuration();
        }
        throw new SeasonTicketNotFoundException("Season ticket is not found");
    }

    public SeasonTicket addSeasonTicket(SeasonTicket seasonTicket)
    {
        return seasonTicketRepository.save(seasonTicket);
    }

    public void changeIfIsForSale(SeasonTicket seasonTicket)
    {
        if (seasonTicket.getIsForSale())
        {
            if (purchaseHistoryRepository.findBySeasonTicket(seasonTicket))
            {
                seasonTicket.setIsForSale(false);
            }
            else
            {
                seasonTicketRepository.removeByTicketType(seasonTicket.getTicketType());
            }
        }
        else
        {
            seasonTicket.setIsForSale(true);
        }
    }

    @Autowired
    public void setSeasonTicketRepository(SeasonTicketRepository seasonTicketRepository)
    {
        this.seasonTicketRepository = seasonTicketRepository;
    }

    @Autowired
    public void setPurchaseHistoryRepository(PurchaseHistoryRepository purchaseHistoryRepository)
    {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }
}
