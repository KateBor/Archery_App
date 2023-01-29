package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.entity.Request;
import spring.entity.RequestStatus;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByStudentId(Long studentId);
    List<Request> findByDayDateAndTimeStartAndTimeEnd(LocalDate date, LocalTime timeStart, LocalTime timeEnd);
    Boolean existsByStudentIdAndDayDateAndTimeStartAndTimeEnd(Long studentId, LocalDate date, LocalTime timeStart, LocalTime timeEnd);
    @Query("select r from Request r where r.day.date = ?1 and ((r.timeStart >= ?2 and r.timeStart <= ?3) or (r.timeEnd >= ?2 and r.timeEnd <= ?3) or (r.timeStart < ?2 and r.timeEnd > ?3))")
    List<Request> findIfIntersectByTime(LocalDate date, LocalTime timeStart, LocalTime timeEnd);
    @Transactional
    @Modifying
    void removeByStudentIdAndDayDateAndTimeStartAndTimeEnd(Long studentId, LocalDate date, LocalTime timeStart, LocalTime timeEnd);
    List<Request> findByDayDate(LocalDate date);
    List<Request> findByStudentIdAndDayDate(Long studentId, LocalDate date);
    @Transactional
    @Modifying
    @Query("update Request r set r.status = :status where r.student.id = :studentId and r.day.date = :date")
    void updateStatusByDate(long studentId, LocalDate date, RequestStatus status);
    @Transactional
    @Modifying
    @Query("update Request r set r.status = :status where r.requestId = :requestId")
    void updateStatus(RequestStatus status, long requestId);
    List<Request> findByStatusStatus(String status);
    @Transactional
    @Modifying
    void removeByRequestId(Long requestId);
    @Query("select r from Request r where r.student.id = :id and r.day.date > :date")
    List<Request> findRequestsWithFutureDays(Long id, LocalDate date);
    @Query("select r from Request r where r.student.id = :id and r.day.date = :date and r.timeStart <= :timeStart and r.timeEnd >= :timeEnd")
    Optional<Request> findIfAPartOfStudentRequest(Long id, LocalDate date, LocalTime timeStart, LocalTime timeEnd);


    @Transactional
    @Modifying
    void removeByStudentIdAndDayDate(Long studentId, LocalDate date);

    @Transactional
    @Modifying
    @Query("delete from Request r where r.student.id = ?1 and r.day.date = ?2 and r.timeStart = ?3")
    void removeByStudentIdAndDayDateAndTimeStart(Long id, LocalDate date, LocalTime timeStart);

    Boolean existsByStudentIdAndDayDate(Long studentId, LocalDate date);

    @Transactional
    @Modifying
    void removeByDayDate(LocalDate date);
}
