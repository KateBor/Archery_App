package spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.entity.Day;
import spring.entity.Request;
import spring.entity.Student;
import spring.repositories.DayRepository;
import spring.repositories.RequestRepository;
import spring.requests.*;
import spring.service.DayService;
import spring.service.PurchaseHistoryService;
import spring.service.RequestService;
import spring.service.StudentService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static spring.Application.*;

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
    @Autowired
    private DayRepository dayRepository;

    @PostMapping("/edit/removelessons")
    public ResponseEntity<String> editTimetableByRemoving(@RequestBody int dayOfWeek)
    {
        areLessons[dayOfWeek - 1] = false;
        List<Request> futureRequests = requestRepository.findAllRequestsWithFutureDays(LocalDate.now());
        for (Request request: futureRequests)
        {
            if (request.getDay().getDate().getDayOfWeek().getValue() == dayOfWeek)
            {
                requestRepository.removeByRequestId(request.getRequestId());
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/edit/changedaytimetable")
    public ResponseEntity<String> editTimetableByChanging(@RequestBody EditDayOfWeekRequest editDayOfWeekRequest)
    {
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HH:mm");
        int i = editDayOfWeekRequest.getDayOfWeek();
        beginnings[i - 1] = LocalTime.parse(editDayOfWeekRequest.getBeginning(), dtf1);
        ends[i - 1] = LocalTime.parse(editDayOfWeekRequest.getEnd(), dtf1);
        areLessons[i - 1] = true;
        List<Request> futureRequests = requestRepository.findAllRequestsWithFutureDays(LocalDate.now());
        for (Request request: futureRequests)
        {
            if (request.getDay().getDate().getDayOfWeek().getValue() == i)
            {
                requestRepository.removeByRequestId(request.getRequestId());
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/day/edit")
    public ResponseEntity<HowToEditDayInfoRequest> canEditDay(@RequestBody String date)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(date, dtf);
        HowToEditDayInfoRequest howToEditDayInfoRequest = new HowToEditDayInfoRequest(false, false);
        if (!LocalDate.now().isBefore(localDate))
        {
            return new ResponseEntity<>(howToEditDayInfoRequest, HttpStatus.OK);
        }
        howToEditDayInfoRequest.setCanEdit(true);
        Day day = dayService.findByDate(localDate);
        if (day.getAreLessons())
        {
            howToEditDayInfoRequest.setRemovingBottom(true);
        }
        return new ResponseEntity<>(howToEditDayInfoRequest, HttpStatus.OK);
    }

    @PostMapping("/day/edit/removelessons")
    public void removeLessons(@RequestBody String date)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(date, dtf);
        requestRepository.removeByDayDate(localDate);
        dayRepository.removeAreLessons(localDate);
    }

    @PostMapping("day/edit/changedaytimetable")
    public void editDay(@RequestBody EditDayRequest editDayRequest)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(editDayRequest.getDate(), dtf);
        requestRepository.removeByDayDate(localDate);
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HH:mm");
        dayRepository.updateDayTimetable(localDate, LocalTime.parse(editDayRequest.getTimeStart()),
                LocalTime.parse(editDayRequest.getTimeEnd(), dtf1));
    }




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
