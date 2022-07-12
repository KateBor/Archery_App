package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.entity.SeasonTicket;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonTicketRepository extends JpaRepository<SeasonTicket, Long>
{
    List<SeasonTicket> findByIsForSale(Boolean isForSale);
    Optional<SeasonTicket> findByTicketType(String ticketType);
    @Transactional
    @Modifying
    void removeByTicketType(String ticketType);
    @Transactional
    @Modifying
    @Query("update SeasonTicket s set s.isForSale = :isForSale where s.ticketType = :ticketType")
    void changeIfIsForSale(String ticketType, Boolean isForSale);
}
