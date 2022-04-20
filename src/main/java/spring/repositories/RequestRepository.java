package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.entity.Request;

import javax.transaction.Transactional;
import java.util.Date;
import java.sql.Time;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByStatusStatus(String status);

    @Transactional
    @Modifying
    @Query("update Request r set r.status = :status where r.requestId = :requestId")
    void updateStatus(String status, long requestId);
    @Transactional
    @Modifying
    @Query("update Request r set r.status = :status where r.requestId = :requestId and r.day.date = :date")
    void updateStatusByDate(String status, long requestId, Date date);
    Boolean existsByStudentIdAndDayDateAndTimeStartAndTimeEnd(Long studentId, Date date, Time timeStart, Time timeEnd);
    Boolean existsByStudentIdAndDayDate(Long studentId, Date date);
    void removeByRequestId(Long requestId);
    void removeByDayDate(Date date);
    void removeByStudentIdAndDayDateAndTimeStartAndTimeEnd(Long studentId, Date date, Time timeStart, Time timeEnd);
    @Transactional
    @Modifying
    @Query("delete from Request r where r.student.id = :studentId and r.day.date >= :date and r.timeStart >= :time")
    void removeActiveRequestsByStudentId(Long studentId, Date date, Time time);
}
