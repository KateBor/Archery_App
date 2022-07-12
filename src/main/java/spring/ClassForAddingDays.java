package spring;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spring.entity.Day;
import spring.repositories.DayRepository;
import spring.repositories.RequestRepository;
import spring.service.DayService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

import static spring.Application.*;

@Component
@RequiredArgsConstructor
public class ClassForAddingDays
{
    private final DayService dayService;
    private final DayRepository dayRepository;
    private final RequestRepository requestRepository;

    @Scheduled(cron = "0 0 0 28 * ?", zone = "GMT+3")
    public void addDay()
    {
        Day day = new Day();
        int year = LocalDate.now().getYear();
        Month month =  LocalDate.now().getMonth();
        if (month.getValue() == 12)
        {
            ++year;
            month = Month.JANUARY;
        }
        else
        {
            month = month.plus(1);
        }
        int length;
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))
        {
            length = month.length(true);
        }
        else
        {
            length = month.length(false);
        }
        for (int i = 1; i <= length; ++i)
        {
            LocalDate date = LocalDate.of(year, month.getValue(), i);
            day.setDate(date);
            int dayOfWeek = date.getDayOfWeek().getValue();
            day.setAreLessons(areLessons[dayOfWeek - 1]);
            day.setTimeStart(beginnings[dayOfWeek - 1]);
            day.setTimeEnd(ends[dayOfWeek - 1]);
            dayService.addDay(day);
        }
    }

    @Scheduled(cron = "0 0 0 28 * ?", zone = "GMT+3")
    public void removeDayAndRequest()
    {
        int year = LocalDate.now().getYear();
        Month month =  LocalDate.now().getMonth();
        if (month.getValue() == 1)
        {
            --year;
            month = Month.DECEMBER;
        }
        else
        {
            month = month.minus(1);
        }
        int length;
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))
        {
            length = month.length(true);
        }
        else
        {
            length = month.length(false);
        }
        for (int i = 1; i <= length; ++i)
        {
            LocalDate date = LocalDate.of(year, month.getValue(), i);
            requestRepository.removeByDayDate(date);
            dayRepository.removeByDate(date);
        }
    }
}
