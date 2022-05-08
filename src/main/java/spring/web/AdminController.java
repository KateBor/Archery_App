package spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.entity.Student;
import spring.exception.StudentNotFoundException;
import spring.requests.UpdateRankRequest;
import spring.service.ProfileStatusService;
import spring.service.StudentService;
import spring.utils.ProfileStatusConstants;

import java.util.List;

@RestController
@RequestMapping("/archery/admin")
public class AdminController {

    @Autowired
    StudentService studentService;
    @Autowired
    ProfileStatusService profileStatusService;

    @GetMapping(value = "/listToApprove")
    public List<Student> needToApproveList() { //??
        return studentService.getStudentsByStatus(ProfileStatusConstants.ON_CHECKING);
    }

    @PostMapping(value = "/setRankAndApprove")
    public ResponseEntity<HttpStatus> setRankAndApprove(@RequestBody UpdateRankRequest request) {
        long id = Long.parseLong(request.getStudent_id());
        try {
            studentService.updateRank(id, request.getRank());
            studentService.updateProfileStatus(id, ProfileStatusConstants.APPROVED);
        } catch (StudentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void disapprove() {
        //надо отправить сообщение на почту почему или позвонить
    }
}