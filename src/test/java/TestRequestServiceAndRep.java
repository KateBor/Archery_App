import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import spring.Application;
import spring.entity.Request;
import spring.entity.RequestStatus;
import spring.entity.Student;
import spring.repositories.RequestRepository;
import spring.service.RequestService;
import spring.service.StudentService;
import spring.utils.Constants;
import spring.utils.Constants.LimitsConst;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static spring.Application.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=Application.class)
public class TestRequestServiceAndRep
{
    @Autowired
    RequestService requestService;

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    StudentService studentService;


    @Test
    void testExistingByTimeAndStudent()
    {
        assertTrue(requestService.existsByStudentIdAndTime(30L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30)));
        assertFalse(requestService.existsByStudentIdAndTime(30L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 0), LocalTime.of(21, 0)));
    }

    @Test
    void testExistingByDateAndStudent()
    {
        assertTrue(requestService.existsByStudentIdAndDate(30L, LocalDate.of(2030, 8, 10)));
        assertFalse(requestService.existsByStudentIdAndDate(30L, LocalDate.of(2030, 8, 8)));
    }

    /*@Test
    void testRemovingByStudentAndTime()
    {
        assertTrue(requestService.existsByStudentIdAndTime(29L, LocalDate.of(2030, 8, 9), LocalTime.of(18, 30), LocalTime.of(20, 30)));
        requestService.removeByStudentIdAndTime(29L, LocalDate.of(2030, 8, 9), LocalTime.of(18, 30), LocalTime.of(20, 30));
        assertFalse(requestService.existsByStudentIdAndTime(29L, LocalDate.of(2030, 8, 9), LocalTime.of(18, 30), LocalTime.of(20, 30)));
    }*/

    @Test
    void testFindingOfIntersect()
    {
        Set<String> requestsTime = requestRepository.findIfIntersectByTime(LocalDate.of(2030, 8, 10),
                LocalTime.of(18, 30), LocalTime.of(20, 30)).stream().map(r ->
            r.getTimeStart().toString() + "-" + r.getTimeEnd().toString()
        ).collect(Collectors.toSet());
        Set<String> waitedRequestsTime = new HashSet<>(Arrays.asList("18:30-20:30", "18:00-20:00", "19:00-21:00", "18:00-21:00"));
        assertTrue(requestsTime.size() == 4 && requestsTime.equals(waitedRequestsTime));
    }

    /*@Test
    void testAddingOfRequest()
    {
        wishedNumberOfJuniors = 1;
        wishedNumberOfDemandingTrainer = 2;
        assertFalse(requestService.addRequest(32L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30)));
        assertFalse(requestService.addRequest(33L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30)));
        assertTrue(requestService.addRequest(37L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30)));
        requestService.removeByStudentIdAndTime(37L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30));

        wishedNumberOfJuniors = 1;
        assertFalse(requestService.addRequest(32L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30)));

        wishedNumberOfDemandingTrainer = 3;
        assertTrue(requestService.addRequest(33L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30)));
        requestService.removeByStudentIdAndTime(33L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30));
        wishedNumberOfJuniors = 5;
        wishedNumberOfDemandingTrainer = 7;

        numberOfShields = 3;
        assertFalse(requestService.addRequest(37L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30)));
        assertTrue(requestService.addRequest(33L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30)));
        requestService.removeByStudentIdAndTime(33L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30));

        numberOfShields = 4;
        assertTrue(requestService.addRequest(32L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30)));
        requestService.removeByStudentIdAndTime(32L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30));
        assertTrue(requestService.addRequest(37L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30)));
        requestService.removeByStudentIdAndTime(37L, LocalDate.of(2030, 8, 10), LocalTime.of(18, 30), LocalTime.of(20, 30));
    }*/

    @Test
    void testFindingStudentsByDate()
    {
        Set<Long> studentsId = requestService.findStudentsByDate(LocalDate.of(2030, 8, 10)).
                stream().map(Student::getId).collect(Collectors.toSet());
        Set<Long> waitedStudentsId = new HashSet<>(Arrays.asList(30L, 28L, 34L, 29L, 36L));
        assertTrue(studentsId.size() == 5 && studentsId.equals(waitedStudentsId));
    }

    @Test
    void testCheckingStatusByStudent()
    {
        RequestStatus requestStatus = requestService.showStatusByStudentIdAndDate(29L, LocalDate.of(2030, 8, 10));
        assertEquals("HAS_COME", requestStatus.getStatus());

        requestStatus = requestService.showStatusByStudentIdAndDate(34L, LocalDate.of(2030, 8, 10));
        assertEquals("HAS_NOT_COME", requestStatus.getStatus());

        requestStatus = requestService.showStatusByStudentIdAndDate(28L, LocalDate.of(2030, 8, 10));
        assertEquals("IS_VIEWED", requestStatus.getStatus());

        requestStatus = requestService.showStatusByStudentIdAndDate(29L, LocalDate.of(2030, 8, 11));
        assertEquals("IS_VIEWED", requestStatus.getStatus());
    }

    @Test
    void testChangingPresence()
    {
       requestService.changePresenceOfStudent(28L, LocalDate.of(2030, 8, 10), true);
       RequestStatus requestStatus = requestService.showStatusByStudentIdAndDate(28L, LocalDate.of(2030, 8, 10));
       assertEquals("HAS_COME", requestStatus.getStatus());
       assertEquals(studentService.findStudentById(28L).getAttended_classes(), 1);

       requestService.changePresenceOfStudent(28L, LocalDate.of(2030, 8, 10), false);
       assertEquals(studentService.findStudentById(28L).getAttended_classes(), 0);
       requestService.updateStatus("IS_VIEWED", 2L);

       requestService.changePresenceOfStudent(29L, LocalDate.of(2030, 8, 11), true);
       requestStatus = requestService.showStatusByStudentIdAndDate(29L, LocalDate.of(2030, 8, 11));
       assertEquals("HAS_COME", requestStatus.getStatus());
       requestService.changePresenceOfStudent(29L, LocalDate.of(2030, 8, 11), false);
       requestService.updateStatus("IS_VIEWED", 9L);
        requestService.updateStatus("IS_VIEWED", 10L);
    }

    @Test
    void testFindingByStatus()
    {
        Set<Long> requestsId = requestService.findByStatus("IS_VIEWED").
                stream().map(Request::getRequestId).collect(Collectors.toSet());
        Set<Long> waitedRequestsId = new HashSet<>(Arrays.asList(2L, 9L, 10L));
        assertTrue(requestsId.size() == 3 && requestsId.equals(waitedRequestsId));
    }

    @Test
    void testGoodSigningUp()
    {
        LocalDate date = LocalDate.of(2022, 5, 18);
        List<String> info = requestService.showInfoAboutSession(30L, date, LocalTime.of(18, 30));
        assertEquals(info.get(4), "Записаться");
        assertEquals(info.get(5), LocalTime.of(20, 29).toString());
        info = requestService.showInfoAboutSession(28L, date, LocalTime.of(18, 30));
        assertEquals(info.get(4), "Записаться");
        assertEquals(info.get(5), LocalTime.of(20, 29).toString());
        info = requestService.showInfoAboutSession(29L, date, LocalTime.of(18, 30));
        assertEquals(info.get(4), "Записаться");
        assertEquals(info.get(5), LocalTime.of(18, 59).toString());

        date = LocalDate.of(2022, 5, 17);
        info = requestService.showInfoAboutSession(34L, date, LocalTime.of(19, 30));
        assertEquals(info.get(4), "Отменить заявку");
    }

//    @Test                     //пока закомментила, надо подумать как доставать значения из класса
//    void testLimitsOfPeople()
//    {
//        LocalDate date = LocalDate.of(2022, 5, 17);
//        wishedNumberOfJuniors = 1;
//        wishedNumberOfDemandingTrainer = 2;
//        List<String> info = requestService.showInfoAboutSession(32L, date, LocalTime.of(19, 0));
//        assertEquals(info.get(0), "1");
//        assertEquals(info.get(1), "1");
//        assertEquals(info.get(2), "2");
//        assertEquals(info.get(3), "3");
//        assertEquals(info.get(4), "Нельзя записаться из-за числа большого новичков в каком-то из получасов");
//
//        info = requestService.showInfoAboutSession(33L, date, LocalTime.of(18, 0));
//        assertEquals(info.get(4), "Нельзя записаться из-за большого числа требующих тренера в каком-то из получасов");
//
//        info = requestService.showInfoAboutSession(33L, date, LocalTime.of(18, 0));
//        assertEquals(info.get(4), "Нельзя записаться из-за большого числа требующих тренера в каком-то из получасов");
//
//        info = requestService.showInfoAboutSession(37L, date, LocalTime.of(19, 00));
//        assertEquals(info.get(4), "Записаться");
//
//
//        wishedNumberOfDemandingTrainer = 3;
//        info = requestService.showInfoAboutSession(33L, date, LocalTime.of(18, 0));
//        assertEquals(info.get(4), "Записаться");
//        wishedNumberOfJuniors = 5;
//        wishedNumberOfDemandingTrainer = 7;
//
//        numberOfShields = 3;
//        info = requestService.showInfoAboutSession(37L, date, LocalTime.of(19, 0));
//        assertEquals(info.get(4), "Нельзя записаться из-за большого числа занятых щитов в каком-то из получасов");
//        info = requestService.showInfoAboutSession(33L, date, LocalTime.of(18, 30));
//        assertEquals(info.get(4), "Записаться");
//
//        numberOfShields = 4;
//        info = requestService.showInfoAboutSession(32L, date, LocalTime.of(18, 30));
//        assertEquals(info.get(4), "Записаться");
//        info = requestService.showInfoAboutSession(37L, date, LocalTime.of(19, 0));
//        assertEquals(info.get(4), "Записаться");
//        numberOfShields = 12;
//    }

    @Test
    void testTimeLimits()
    {
      List<String> info = requestService.showInfoAboutSession(29L, LocalDate.of(2020, 1, 1), LocalTime.of(18, 0));
      assertEquals(info.get(4), "Редактирование заявки недоступно: занятие уже началось или прошло");

      // requestService.addRequest(29L, LocalDate.now(), LocalTime.of(19, 30), LocalTime.of(21, 29));
       info = requestService.showInfoAboutSession(29L, LocalDate.now(),  LocalTime.of(20, 0));
       assertEquals(info.get(4), "Редактирование заявки недоступно: занятие уже началось или прошло");
    }

    @Test
    void testLimitsWithTicket()
    {
        List<String> info = requestService.showInfoAboutSession(29L, LocalDate.of(2030, 1, 1), LocalTime.of(18, 0));
        assertEquals(info.get(4), "Нет активного абонемента на момент этой даты");
        info = requestService.showInfoAboutSession(31L, LocalDate.of(2022, 5, 17), LocalTime.of(18, 30));
        assertEquals(info.get(4), "Нет активного абонемента на момент этой даты");
        info = requestService.showInfoAboutSession(41L, LocalDate.of(2022, 5, 17), LocalTime.of(18, 0));
        assertEquals(info.get(4), "Число записей на будущие занятия достигло лимита");
    }

    @Test
    void testLimitsWithRequests()
    {
        List<String> info = requestService.showInfoAboutSession(28L, LocalDate.of(2022, 5, 17), LocalTime.of(20, 0));
        assertEquals(info.get(4), "Вы уже записались на сегодня");
    }


    /*@Test
    void testCountingOfRanks()
    {
        wishedNumberOfJuniors = 1;
        wishedNumberOfDemandingTrainer = 2;
        List<String> info = requestService.showInfoAboutSession(32L, LocalDate.of(2030, 7, 20),
                LocalTime.of(18, 0), LocalTime.of(18, 30));
        assertEquals(info.get(0), "1");
        assertEquals(info.get(1), "1");
        assertEquals(info.get(2), "2");
        assertEquals(info.get(3), "3");
        assertEquals(info.get(4), "Нельзя записаться из-за превышенного числа учеников по рангу или по щитам");

        info = requestService.showInfoAboutSession(33L, LocalDate.of(2030, 7, 20),
                LocalTime.of(18, 0), LocalTime.of(18, 30));
        assertEquals(info.get(4), "Нельзя записаться из-за превышенного числа учеников по рангу или по щитам");

        info = requestService.showInfoAboutSession(37L, LocalDate.of(2030, 7, 20),
                LocalTime.of(18, 0), LocalTime.of(18, 30));
        assertEquals(info.get(4), "Записаться");

        wishedNumberOfJuniors = 1;
        info = requestService.showInfoAboutSession(32L, LocalDate.of(2030, 7, 20),
                LocalTime.of(18, 0), LocalTime.of(18, 30));
        assertEquals(info.get(4), "Нельзя записаться из-за превышенного числа учеников по рангу или по щитам");


        wishedNumberOfDemandingTrainer = 3;
        info = requestService.showInfoAboutSession(33L, LocalDate.of(2030, 7, 20),
                LocalTime.of(18, 0), LocalTime.of(18, 30));
        assertEquals(info.get(4), "Записаться");
        wishedNumberOfJuniors = 5;
        wishedNumberOfDemandingTrainer = 7;

        numberOfShields = 3;
        info = requestService.showInfoAboutSession(37L, LocalDate.of(2030, 7, 20),
                LocalTime.of(18, 0), LocalTime.of(18, 30));
        assertEquals(info.get(4), "Нельзя записаться из-за превышенного числа учеников по рангу или по щитам");
        info = requestService.showInfoAboutSession(33L, LocalDate.of(2030, 7, 20),
                LocalTime.of(18, 0), LocalTime.of(18, 30));
        assertEquals(info.get(4), "Записаться");

        numberOfShields = 4;
        info = requestService.showInfoAboutSession(32L, LocalDate.of(2030, 7, 20),
                LocalTime.of(18, 0), LocalTime.of(18, 30));
        assertEquals(info.get(4), "Записаться");
        info = requestService.showInfoAboutSession(37L, LocalDate.of(2030, 7, 20),
                LocalTime.of(18, 0), LocalTime.of(18, 30));
        assertEquals(info.get(4), "Записаться");

        numberOfShields = 12;
        info = requestService.showInfoAboutSession(28L, LocalDate.of(2030, 7, 20),
                LocalTime.of(18, 0), LocalTime.of(18, 30));
        assertEquals(info.get(4), "Отменить заявку");
    }

    @Test
    void testActuality()
    {
        List<String> info = requestService.showInfoAboutSession(32L, LocalDate.of(2010, 7, 20),
                LocalTime.of(18, 0), LocalTime.of(18, 30));
        assertEquals(info.get(4), "Редактирование заявки недоступно: занятие уже началось или прошло");
        info = requestService.showInfoAboutSession(32L, LocalDate.of(2022, 5, 9),
                LocalTime.of(10, 0), LocalTime.of(10, 30));
        assertEquals(info.get(4), "Редактирование заявки недоступно: занятие уже началось или прошло");
    }

    @Test
    void testTicketLimits()
    {
        LocalDate date =  LocalDate.of(2022, 5, 16);
        List<String> info = requestService.showInfoAboutSession(28L, date,
                LocalTime.of(20, 0), LocalTime.of(20, 30));
        assertEquals(info.get(4), "Число ваших заявок на сегодня достигло лимита");

        info = requestService.showInfoAboutSession(30L, date,
                LocalTime.of(20, 0), LocalTime.of(20, 30));
        assertEquals(info.get(4), "Число ваших заявок на сегодня достигло лимита");

        info = requestService.showInfoAboutSession(29L, date,
                LocalTime.of(20, 0), LocalTime.of(20, 30));
        assertEquals(info.get(4), "Записаться");

        info = requestService.showInfoAboutSession(32L, date,
                LocalTime.of(20, 0), LocalTime.of(20, 30));
        assertEquals(info.get(4), "Записаться");

        info = requestService.showInfoAboutSession(31L, date,
                LocalTime.of(20, 0), LocalTime.of(20, 30));
        assertEquals(info.get(4), "Нет активного абонемента на момент этой даты");
    }*/
}
