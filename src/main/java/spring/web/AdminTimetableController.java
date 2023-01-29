package spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.entity.Day;
import spring.entity.Request;
import spring.entity.SeasonTicket;
import spring.entity.Student;
import spring.exception.DayNotFoundException;
import spring.repositories.RequestRepository;
import spring.requests.LessonRequest;
import spring.requests.StudentAndDateRequest;
import spring.service.DayService;
import spring.service.PurchaseHistoryService;
import spring.service.RequestService;
import spring.service.StudentService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/archery/admin/timetable")
public class AdminTimetableController
{
    @Autowired
    private DayService dayService;
    @Autowired
    private PurchaseHistoryService purchaseHistoryService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private RequestRepository requestRepository;

    @GetMapping("/day/students")
    public ResponseEntity<List<Student>> showStudentsAtDay(@RequestBody String date)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(date, dtf);
        List<Student> students = requestService.findStudentsByDate(localDate);
        /*List<String> stringStudents = new ArrayList<>();
        for (Student student: students)
        {
            stringStudents.add(student.getFirst_name() + " " + student.getLast_name() + "\n");
        }
        return new ResponseEntity<>(lessons + "\n" + stringStudents, HttpStatus.OK); */
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/day/id")
    public ResponseEntity<List<String>> showPresenceOfStudent(@RequestBody StudentAndDateRequest studentAndDateRequest)
    {
        List<String> action = new ArrayList<>();
        Long id = studentAndDateRequest.getId();
        LocalDate date = studentAndDateRequest.getDate();
        if (LocalDate.now().isBefore(date))
        {
            return new ResponseEntity<>(action, HttpStatus.OK);
        }
        if (LocalDate.now().isEqual(date))
        {
            if (requestService.checkIfTodayRequestIsActive(id))
            {
                return new ResponseEntity<>(action, HttpStatus.OK);
            }
        }
        if (!requestService.showStatusByStudentIdAndDate(id, date).getStatus().equals("HAS_COME"))
        {
            action.add("Добавить кнопку 'пришел'");
            if (requestService.showStatusByStudentIdAndDate(id, date).getStatus().equals("HAS_NOT_COME"))
            {
                action.add("Текущий статус - не пришел");
            }
        }
        if (!requestService.showStatusByStudentIdAndDate(id, date).getStatus().equals("HAS_NOT_COME"))
        {
            action.add("Добавить кнопку 'не пришел'");
            if (requestService.showStatusByStudentIdAndDate(id, date).getStatus().equals("HAS_COME"))
            {
                action.add("Текущий статус - пришел");
            }
        }
        return new ResponseEntity<>(action, HttpStatus.OK);
    }

    @PostMapping("/day/id")
    public void changePresenceOfStudent(@RequestBody StudentAndDateRequest studentAndDateRequest)
    {
        requestService.changePresenceOfStudent(studentAndDateRequest.getId(), studentAndDateRequest.getDate(), studentAndDateRequest.getHasCome());
    }

    @GetMapping("/day/lesson")
    public ResponseEntity<List<String>> showLesson(@RequestBody LessonRequest request)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date = LocalDate.parse(request.getDate(), dtf);
        LocalTime timeStart = LocalTime.parse(request.getTimeStart(), dtf1);
        return new ResponseEntity<>(requestService.showShortInfoAboutSession(date, timeStart), HttpStatus.OK);
    }

    @GetMapping("/day/lesson/students")
    public ResponseEntity<List<Student>> showLessonStudents(@RequestBody LessonRequest request)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date = LocalDate.parse(request.getDate(), dtf);
        LocalTime timeStart = LocalTime.parse(request.getTimeStart(), dtf1);
        return new ResponseEntity<>(requestRepository.findIfIntersectByTime(date, timeStart, timeStart.plusMinutes(29L)).
                stream().map(Request::getStudent).toList(), HttpStatus.OK);
    }
}
