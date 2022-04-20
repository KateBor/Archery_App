package spring.service;

import spring.entity.Request;

import java.util.Date;
import java.sql.Time;
import java.util.List;

public interface RequestService {
    List<Request> findByStatus(String status);
    void updateStatus(String status, long requestId);
    Boolean existsByStudentAndTime(Long studentId, Date date, Time timeStart, Time timeEnd);
    Boolean existsByStudentAndDate(Long studentId, Date date);
    void removeRequest(Long requestId);
    void removeByDate(Date date);
    void removeByStudentAndTime(Long studentId, Date date, Time timeStart, Time timeEnd);
    void removeActiveRequestsByStudent(Long studentId, Date date, Time time);
    void changeIfHasCome(Long studentId, Date date, Boolean hasCome);
}
