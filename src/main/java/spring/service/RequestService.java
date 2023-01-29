package spring.service;

import spring.entity.Day;
import spring.entity.Request;
import spring.entity.RequestStatus;
import spring.entity.Student;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public interface RequestService {
    List<Request> findByTime(LocalDate date, LocalTime timeStart, LocalTime timeEnd);
    Boolean existsByStudentIdAndTime(Long studentId, LocalDate date, LocalTime timeStart, LocalTime timeEnd);

    void removeByStudentIdAndTime(Long studentId, LocalDate date, LocalTime timeStart, LocalTime timeEnd);
    List<Student> findStudentsByDate(LocalDate date);
    RequestStatus showStatusByStudentIdAndDate(Long studentId, LocalDate date);
    void changePresenceOfStudent(Long studentId, LocalDate date, Boolean hasCome);
    List<Student> findStudentsByTime(LocalDate date, LocalTime timeStart, LocalTime timeEnd);
    void removeRequest(Long requestId);
    void updateStatus(String status, long requestId);
    List<Request> findByStatus(String status);
    List<Request> findByStudentIdAndDate(Long studentId, LocalDate date);

    Boolean existsByStudentIdAndDate(Long studentId, LocalDate date);
    List<LocalDate> findDaysWithActiveRequests(Long studentId);


    Boolean checkIfTodayRequestIsActive(Long studentId);



    List<String> showShortInfoAboutSession(LocalDate date, LocalTime timeStart);
    List<String> showInfoAboutSession(Long studentId, LocalDate date, LocalTime timeStart);
    void addRequest(Long studentId, LocalDate date, LocalTime timeStart, LocalTime timeEnd);
    void removeByStudentIdAndDateTimeStart(Long studentId, LocalDate date, LocalTime timeStart);





    /*void removeByDate(LocalDate date);
    void removeActiveRequestsByStudent(Long studentId, LocalDate date, LocalTime time);*/

}
