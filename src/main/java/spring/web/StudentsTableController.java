package spring.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import spring.entity.PurchaseHistory;
import spring.entity.Rank;
import spring.entity.SeasonTicket;
import spring.entity.Student;
import spring.exception.PurchaseNotFoundException;
import spring.exception.StudentNotFoundException;
import spring.repositories.SeasonTicketRepository;
import spring.repositories.StudentRepository;
import spring.requests.StudentAndRankRequest;
import spring.requests.StudentAndTicketRequest;
import spring.requests.TicketInfoRequest;
import spring.service.PurchaseHistoryService;
import spring.service.RankService;
import spring.service.StudentService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/archery/admin/students")
@RequiredArgsConstructor
public class StudentsTableController {
    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final SeasonTicketRepository ticketRepository;
    private final PurchaseHistoryService purchaseHistoryService;
    private final RankService rankService;

    @GetMapping("")
    public ResponseEntity<List<Student>> showStudents() {
        return new ResponseEntity<>(studentRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Student> showStudent(@PathVariable Long id, Authentication auth) {
        if (auth == null) { // не должно никогда срабатывать, тк отлавливается в JwtFilter
            //значит время сеанса истекло и тут перенаправлять на страницу с логином
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            return new ResponseEntity<>(studentService.findStudentById(id), HttpStatus.OK);
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @GetMapping("/ticket/{id}")
    public ResponseEntity<TicketInfoRequest> showTicket(@PathVariable Long id, Authentication auth) {
        if (auth == null) { // не должно никогда срабатывать, тк отлавливается в JwtFilter
            //значит время сеанса истекло и тут перенаправлять на страницу с логином
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            String ticket = "";
            TicketInfoRequest ticketInfoRequest = new TicketInfoRequest("", 0);
            try {
                PurchaseHistory purchaseHistory = purchaseHistoryService.findPurchaseWithActiveSeasonTicket(id, LocalDate.now());
                ticketInfoRequest.setTicketType(purchaseHistory.getSeasonTicket().getTicketType());
                ticketInfoRequest.setAvailableClasses(purchaseHistory.getAvailableClasses());
            } catch (PurchaseNotFoundException ignored) {
            }
            return new ResponseEntity<>(ticketInfoRequest, HttpStatus.OK);
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<String>> findTickets() {
        return new ResponseEntity<>(ticketRepository.findAll().stream().map(SeasonTicket::getTicketType).toList(), HttpStatus.OK);
    }

    @PostMapping("/changeticket")
    public ResponseEntity<HttpStatus> editTicket(@RequestBody StudentAndTicketRequest studentAndTicketRequest, Authentication auth) {
        if (auth == null) { // не должно никогда срабатывать, тк отлавливается в JwtFilter
            //значит время сеанса истекло и тут перенаправлять на страницу с логином
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            Student student = studentService.findStudentById(studentAndTicketRequest.getId());
            SeasonTicket seasonTicket = ticketRepository.findByTicketType(studentAndTicketRequest.getSeasonTicketType()).get();
            purchaseHistoryService.addPurchase(student, LocalDate.now(), seasonTicket);
            System.out.println("SeasonTicket is updated");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @GetMapping("/ranks")
    public ResponseEntity<List<String>> findRanks() {
        return new ResponseEntity<>(rankService.findAll().stream().map(Rank::getRank_name).toList(), HttpStatus.OK);
    }

    @PostMapping("/changerank")
    public void edit(@RequestBody StudentAndRankRequest studentAndRankRequest) {

        studentService.updateRank(studentAndRankRequest.getId(), studentAndRankRequest.getRank());
    }
}
