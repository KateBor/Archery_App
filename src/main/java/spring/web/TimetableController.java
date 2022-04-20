package spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.entity.Day;
import spring.service.DayService;

import java.util.List;

@RestController
@RequestMapping("/archery/admin/timetable")
public class TimetableController
{
    private DayService dayService;

    @Autowired
    public void setDayService(DayService dayService)
    {
        this.dayService = dayService;
    }
}
