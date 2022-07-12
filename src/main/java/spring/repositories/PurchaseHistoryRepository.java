package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.entity.PurchaseHistory;
import spring.entity.SeasonTicket;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, String>
{
    @Query("select p from PurchaseHistory p where p.student.id = :studentId and p.availableClasses > 0")
    List<PurchaseHistory> findUnspentSeasonTicket(Long studentId);
    Boolean existsByStudentId(Long studentId);
    Optional<PurchaseHistory> findTopByStudentIdOrderByStartDateDesc(Long studentId);
    Boolean findBySeasonTicket(SeasonTicket seasonTicket);







/*    List<PurchaseHistory> findByStudentId(Long studentId);

     */
}
