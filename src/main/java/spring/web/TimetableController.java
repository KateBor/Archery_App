package spring.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.entity.Day;
import spring.entity.Student;
import spring.exception.DayNotFoundException;
import spring.requests.LessonWithEndRequest;
import spring.requests.LessonRequest;
import spring.service.DayService;
import spring.service.RequestService;
import spring.service.StudentService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/archery/timetable")
@RequiredArgsConstructor
public class TimetableController
{
    private final DayService dayService;
    private final RequestService requestService;
    private final StudentService studentService;

    @GetMapping("")
    public ResponseEntity<List<LocalDate>> showTimetable() {
        return new ResponseEntity<>(dayService.showMonth(), HttpStatus.OK);
    }

    @GetMapping("/day")
    public ResponseEntity<Boolean> showDay(@RequestBody String date) //вроде можно из js пересылать USVString, либо сделать обертку
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(date, dtf);
        try {
            Day day = dayService.findByDate(localDate);
            if (!day.getAreLessons()) {
                return new ResponseEntity<>(false, HttpStatus.NOT_ACCEPTABLE); //406
            }
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (DayNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400
        }
    }

    @GetMapping("/day/lesson")
    public ResponseEntity<List<String>> showLesson(@RequestBody LessonRequest request)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Student student = studentService.findStudentByEmail(auth.getName());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date = LocalDate.parse(request.getDate(), dtf);
        LocalTime timeStart = LocalTime.parse(request.getTimeStart(), dtf1);
        return new ResponseEntity<>(requestService.showInfoAboutSession(student.getId(), date, timeStart), HttpStatus.OK);
    }

    @PostMapping("/day/lesson/signUp")
    public ResponseEntity<String> signUpLesson(@RequestBody LessonWithEndRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Student student = studentService.findStudentByEmail(auth.getName());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date = LocalDate.parse(request.getDate(), dtf);
        LocalTime timeStart = LocalTime.parse(request.getTimeStart(), dtf1);
        LocalTime timeEnd = LocalTime.parse(request.getTimeEnd(), dtf1);
        requestService.addRequest(student.getId(), date, timeStart, timeEnd);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/day/lesson/remove")
    public ResponseEntity<String> removeRequest(@RequestBody LessonRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Student student = studentService.findStudentByEmail(auth.getName());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date = LocalDate.parse(request.getDate(), dtf);
        LocalTime timeStart = LocalTime.parse(request.getTimeStart(), dtf1);
        requestService.removeByStudentIdAndDateTimeStart(student.getId(), date, timeStart);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
