package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.entity.SeasonTicket;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonTicketRepository extends JpaRepository<SeasonTicket, Long>
{
    List<SeasonTicket> findByIsForSale(Boolean isForSale);
    Optional<SeasonTicket> findByTicketType(String ticketType);
    void removeByTicketType(String ticketType);
}
