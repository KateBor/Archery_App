package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.entity.Day;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DayRepository extends JpaRepository<Day, String>
{
    Optional<Day> findByDate(LocalDate date);

    @Query("select d from Day d where d.date >= :start and d.date <= :end")
    List<Day> findFromTo(LocalDate start, LocalDate end);

    @Transactional
    @Modifying
    void removeByDate(LocalDate date);

    @Transactional
    @Modifying
    @Query("update Day d set d.areLessons = false where d.date = :date")
    void removeAreLessons(LocalDate date);

    @Transactional
    @Modifying
    @Query("update Day d set d.areLessons = true, d.timeStart = :timeStart, d.timeEnd = :timeEnd where d.date = :date")
    void updateDayTimetable(LocalDate date, LocalTime timeStart, LocalTime timeEnd);


    //Boolean findByDateAndAreLessons(LocalDate date, Boolean areLessons);
}
