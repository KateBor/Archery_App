package spring.service;

import spring.entity.Day;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DayService {
    Day findByDate(Date date);
    List<Day> findFromTo(Date start, Date end);
    Boolean areLessons(Date date);
    void changeAreLessons(Date date);
    void changeTimeStart(Date date, LocalTime timeStart);
    void changeTimeEnd(Date date, LocalTime timeEnd);
}
