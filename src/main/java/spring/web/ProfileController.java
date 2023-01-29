package spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.entity.PurchaseHistory;
import spring.entity.Student;
import spring.exception.PurchaseNotFoundException;
import spring.exception.StudentNotFoundException;
import spring.requests.AuthRequest;
import spring.requests.TicketInfoRequest;
import spring.requests.UpdateProfileRequest;
import spring.security.jwt.JwtTokenProvider;
import spring.service.PurchaseHistoryService;
import spring.service.SecurityService;
import spring.service.StudentService;

import javax.management.remote.JMXAuthenticator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/archery/profile")
public class ProfileController {

    @Autowired
    StudentService studentService;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    SecurityService securityService;
    @Autowired
    PurchaseHistoryService purchaseHistoryService;

    @GetMapping(value = "/getFirstName")
    public ResponseEntity<String> getFirstName(Authentication auth) {
        if (auth == null) { // не должно никогда срабатывать, тк отлавливается в JwtFilter
            //значит время сеанса истекло и тут перенаправлять на страницу с логином
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            Student student = studentService.findStudentByEmail(auth.getName());
            return ResponseEntity.ok(student.getFirst_name());
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @GetMapping(value = "/getLastName")
    public ResponseEntity<String> getLastName(Authentication auth) {
        if (auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            Student student = studentService.findStudentByEmail(auth.getName());
            return ResponseEntity.ok(student.getLast_name());
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @GetMapping(value = "/getPhoneNumber")
    public ResponseEntity<String> getPhoneNumber(Authentication auth) {
        if (auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            Student student = studentService.findStudentByEmail(auth.getName());
            return ResponseEntity.ok(student.getPhone_number());
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @GetMapping(value = "/getEmail")
    public ResponseEntity<String> getEmail(Authentication auth) {
        if (auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            Student student = studentService.findStudentByEmail(auth.getName());
            return ResponseEntity.ok(student.getEmail());
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @GetMapping(value = "/getBirthDate")
    public ResponseEntity<String> getBirthDate(Authentication auth) {
        if (auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            Student student = studentService.findStudentByEmail(auth.getName());
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            return ResponseEntity.ok(df.format(student.getBirth_date()));
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @GetMapping(value = "/getAttendedClasses")
    public ResponseEntity<String> getAttendedClasses(Authentication auth) {
        if (auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            Student student = studentService.findStudentByEmail(auth.getName());
            return ResponseEntity.ok(Integer.toString(student.getAttended_classes()));
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @GetMapping(value = "/getRankName")
    public ResponseEntity<String> getRankName(Authentication auth) {
        if (auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            Student student = studentService.findStudentByEmail(auth.getName());
            return ResponseEntity.ok(student.getRank_name().getRank_name());
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @GetMapping(value = "/getProfileStatus")
    public ResponseEntity<String> getProfileStatus(Authentication auth) {
        if (auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            Student student = studentService.findStudentByEmail(auth.getName());
            return ResponseEntity.ok(student.getProfile_status().getStatus());
        } catch (
                StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @GetMapping(value = "/getTicket")
    public ResponseEntity<TicketInfoRequest> getTicket(Authentication auth) {
        if (auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //401
        }
        try {
            Student student = studentService.findStudentByEmail(auth.getName());
            String ticket = "";
            TicketInfoRequest ticketInfoRequest = new TicketInfoRequest("", 0);
            try
            {
                PurchaseHistory purchaseHistory = purchaseHistoryService.findPurchaseWithActiveSeasonTicket(student.getId(), LocalDate.now());
                ticketInfoRequest.setTicketType(purchaseHistory.getSeasonTicket().getTicketType());
                ticketInfoRequest.setAvailableClasses(purchaseHistory.getAvailableClasses());
            }
            catch (PurchaseNotFoundException ignored) {}
            return ResponseEntity.ok(ticketInfoRequest);
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @PostMapping(value = "/updateAll", consumes = "application/json")
    public ResponseEntity<String> updateAll(@RequestBody UpdateProfileRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            Student student = studentService.findStudentByEmail(auth.getName());

            studentService.updateFirstName(student.getId(), request.getFirst_name());
            studentService.updateLastName(student.getId(), request.getLast_name());
            studentService.updatePhoneNumber(student.getId(), request.getPhone_number());
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            studentService.updateBirthDate(student.getId(), df.parse(request.getBirth_date()));

//            studentService.updateEmail(student.getId(), request.getEmail()); //обновить токен
//            String token = jwtTokenProvider.createToken(request.getEmail(), student.getRoles());
//
//            securityService.autologin(request.getEmail(), request.getPassword());
//            studentService.updateToken(student.getId(), token);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/updateEmail", consumes = "application/json")
    public ResponseEntity<String> updateEmail(@RequestBody AuthRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            studentService.findStudentByEmail(request.getLogin());
        } catch (StudentNotFoundException ex) {
            try {
                String email = request.getLogin();
                String password = request.getPassword();
                Student student = studentService.findStudentByEmail(auth.getName());
                studentService.updateEmail(student.getId(), email);
                String token = jwtTokenProvider.createToken(email, student.getRoles());
                securityService.autologin(email, password);
                studentService.updateToken(student.getId(), token);

                //потом сделать отправку сообщения
                // на почту с подтверждением
                return ResponseEntity.ok(token);

            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED); //
    }


    //Post
//
//    @PostMapping(value = "/updateFirstName", consumes = "application/json") //consume type?
//    public ResponseEntity<String> updateFirstName(@RequestBody String firstName) {
//        Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        studentService.updateFirstName(student.getId(), firstName);
//        return ResponseEntity.ok(firstName);
//    }
//
//    @PostMapping(value = "/updateLastName", consumes = "application/json")
//    public ResponseEntity<String> updateLastName(@RequestBody String lastName) {
//        Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        studentService.updateLastName(student.getId(), lastName);
//        return ResponseEntity.ok(lastName);
//    }
//
//    @PostMapping(value = "/updatePhoneNumber", consumes = "application/json")
//    public ResponseEntity<String> updatePhoneNumber(@RequestBody String phoneNumber) {
//        Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        studentService.updatePhoneNumber(student.getId(), phoneNumber);
//        return ResponseEntity.ok(phoneNumber);
//    }
//
//    @PostMapping(value = "/updateBirthDate", consumes = "application/json")
//    public ResponseEntity<String> updateBirthDate(@RequestBody String birthDate) {
//        Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
//        Date date;
//        try {
//            date = df.parse(birthDate);
//            //проверка на дату как при создании добавить
//        } catch (ParseException e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        studentService.updateBirthDate(student.getId(), date);
//        return ResponseEntity.ok(birthDate);
//    }
}
