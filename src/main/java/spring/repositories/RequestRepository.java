package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.entity.Request;

import javax.transaction.Transactional;
import java.util.Date;
import java.sql.Time;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByStatus(String status);

    @Transactional
    @Modifying
    @Query("update Request r set r.status = :status where r.requestId = :requestId")
    void updateStatus(String status, long requestId);

    Boolean existsByStudentIdAndDateAndTimeStartAndTimeEnd(Long studentId, Date date, Time timeStart, Time timeEnd);
    Boolean existsByStudentIdAndDate(Long studentId, Date date);
    void removeByRequestId(Long requestId);
    void removeByDate(Date date);
    void removeByStudentIdAndDateAndTimeStartAndTimeEnd(Long studentId, Date date, Time timeStart, Time timeEnd);

    @Transactional
    @Modifying
    @Query("delete from Request r where r.date >= :date and r.timeStart >= :time")
    void removeActiveRequestsByStudent(Long studentId, Date date, Time time);

}
