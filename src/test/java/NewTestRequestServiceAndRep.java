import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import spring.Application;
import spring.entity.Student;
import spring.service.RequestService;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= Application.class)
public class NewTestRequestServiceAndRep
{
    @Autowired
    RequestService requestService;

    @Test
    void testIfTodayIsActive()
    {
        //Assertions.assertFalse(requestService.checkIfTodayRequestIsActive(51L));
        //Assertions.assertFalse(requestService.checkIfTodayRequestIsActive(52L));
    }

    @Test
    void testTodayStudents()
    {
        Assertions.assertEquals(requestService.findStudentsByDate(LocalDate.of(2022, 6, 27)).
                stream().map(Student::getId).sorted().toList(), List.of(51L, 52L));
    }

    @Test
    void testFindingOfActiveDays()
    {
       // Assertions.assertEquals(requestService.findDaysWithActiveRequests(51L).stream().sorted().toList(), List.of(LocalDate.of(2022, 6, 27),
         //       LocalDate.of(2022, 6, 30)));
    }

    @Test
    void testChangingOfPresence()
    {
        //requestService.changePresenceOfStudent(53L, LocalDate.now(), true);
        //Assertions.assertEquals(requestService.findDaysWithActiveRequests(53L).stream().sorted().toList(), List.of(
       //         LocalDate.of(2022, 6, 28), LocalDate.of(2022, 6, 29)));
    }
}
