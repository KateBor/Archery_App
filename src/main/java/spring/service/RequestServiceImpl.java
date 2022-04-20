package spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.entity.Request;
import spring.repositories.RequestRepository;

import java.util.Date;
import java.sql.Time;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {
    private RequestRepository requestRepository;
    private StudentService studentService;

    public List<Request> findByStatus(String status)
    {
        return requestRepository.findByStatusStatus(status);
    }

    public void updateStatus(String status, long requestId)
    {
        requestRepository.updateStatus(status, requestId);
    }

    public Boolean existsByStudentAndTime(Long studentId, Date date, Time timeStart, Time timeEnd)
    {
        return requestRepository.existsByStudentIdAndDayDateAndTimeStartAndTimeEnd(studentId, date, timeStart, timeEnd);
    }

    public Boolean existsByStudentAndDate(Long studentId, Date date)
    {
        return requestRepository.existsByStudentIdAndDayDate(studentId, date);
    }

    public void removeRequest(Long requestId)
    {
        requestRepository.removeByRequestId(requestId);
    }

    public void removeByDate(Date date)
    {
        requestRepository.removeByDayDate(date);
    }

    public void removeByStudentAndTime(Long studentId, Date date, Time timeStart, Time timeEnd)
    {
        requestRepository.removeByStudentIdAndDayDateAndTimeStartAndTimeEnd(studentId, date, timeStart, timeEnd);
    }

    public void removeActiveRequestsByStudent(Long studentId, Date date, Time time)
    {
        requestRepository.removeActiveRequestsByStudentId(studentId, date, time);
    }

    public void changeIfHasCome(Long studentId, Date date, Boolean hasCome)
    {
        //через репозиторий обновить статус всех заявок в этот день на "пришел" или "не пришел" (зависит от hasCome)
        if (hasCome)
        {
            studentService.changeAttendedClasses(studentId, true);
        }
    }

    @Autowired
    public void setRequestRepository(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }
    @Autowired
    public void setStudentService(StudentService studentService)
    {
        this.studentService = studentService;
    }
}
