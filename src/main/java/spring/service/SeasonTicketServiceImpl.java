package spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.entity.SeasonTicket;
import spring.exception.SeasonTicketNotFoundException;
import spring.repositories.SeasonTicketRepository;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Service
public class SeasonTicketServiceImpl implements SeasonTicketService {
    SeasonTicketRepository seasonTicketRepository;

    public List<SeasonTicket> areForSale()
    {
        return seasonTicketRepository.findByIsForSale((byte) 1);
    }

    public List<SeasonTicket> areNotForSale()
    {
        return seasonTicketRepository.findByIsForSale((byte) 0);
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

    @Autowired
    public void setSeasonTicketRepository(SeasonTicketRepository seasonTicketRepository)
    {
        this.seasonTicketRepository = seasonTicketRepository;
    }
}
