package spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.entity.*;
import spring.exception.PurchaseNotFoundException;
import spring.exception.RequestNotFoundException;
import spring.exception.RequestStatusNotFound;
import spring.repositories.RequestRepository;
import spring.repositories.RequestStatusRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static spring.Application.*;
import static spring.utils.Constants.LimitsConst.*;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService
{

    private final RequestRepository requestRepository;
    private final StudentService studentService;
    private final DayService dayService;
    private final RequestStatusRepository requestStatusRepository;
    private final PurchaseHistoryService purchaseHistoryService;


    public List<Request> findByTime(LocalDate date, LocalTime timeStart, LocalTime timeEnd)
    {
        return requestRepository.findByDayDateAndTimeStartAndTimeEnd(date, timeStart, timeEnd);
    }

    public Boolean existsByStudentIdAndTime(Long studentId, LocalDate date, LocalTime timeStart, LocalTime timeEnd)
    {
        return requestRepository.existsByStudentIdAndDayDateAndTimeStartAndTimeEnd(studentId, date, timeStart, timeEnd);
    }




    public void removeByStudentIdAndTime(Long studentId, LocalDate date, LocalTime timeStart, LocalTime timeEnd)
    {
        requestRepository.removeByStudentIdAndDayDateAndTimeStartAndTimeEnd(studentId, date, timeStart, timeEnd);
    }

    public List<Student> findStudentsByDate(LocalDate date)
    {
        List<Request> requests = requestRepository.findByDayDate(date);
        return requests.stream().map(Request::getStudent).distinct().collect(Collectors.toList());
    }

    public List<Request> findByStudentIdAndDate(Long studentId, LocalDate date)
    {
        return requestRepository.findByStudentIdAndDayDate(studentId, date);
    }

    public RequestStatus showStatusByStudentIdAndDate(Long studentId, LocalDate date)
    {
        List<Request> requests = requestRepository.findByStudentIdAndDayDate(studentId, date);
        if (requests.isEmpty())
        {
            throw new RequestNotFoundException("Request is not found");
        }
        return requests.get(0).getStatus();
    }

    public List<Student> findStudentsByTime(LocalDate date, LocalTime timeStart, LocalTime timeEnd)
    {
        return requestRepository.findIfIntersectByTime(date, timeStart, timeEnd).stream().map(Request::getStudent).toList();
    }

    public void removeRequest(Long requestId)
    {
        requestRepository.removeByRequestId(requestId);
    }

    public void updateStatus(String status, long requestId)
    {
        Optional<RequestStatus> requestStatusOptional = requestStatusRepository.findByStatus(status);
        if (requestStatusOptional.isEmpty())
        {
            throw new RequestStatusNotFound("Request status is not found");
        }
        requestRepository.updateStatus(requestStatusOptional.get(), requestId);
    }

    public List<Request> findByStatus(String status)
    {
        return requestRepository.findByStatusStatus(status);
    }
    public Boolean existsByStudentIdAndDate(Long studentId, LocalDate date)
    {
        return requestRepository.existsByStudentIdAndDayDate(studentId, date);
    }








    public Boolean checkIfTodayRequestIsActive(Long studentId)
    {
        List<Request> todayRequests = requestRepository.findByStudentIdAndDayDate(studentId, LocalDate.now());
        if (todayRequests.isEmpty())
        {
            return false;
        }
        for (Request request: todayRequests)
        {
            if (LocalTime.now().isAfter(request.getTimeStart()))
            {
                return false;
            }
        }
        return true;
    }

    public List<LocalDate> findDaysWithActiveRequests(Long studentId)
    {
        List<LocalDate> daysWithActiveRequests = new ArrayList<>(requestRepository.findRequestsWithFutureDays(studentId, LocalDate.now()).
                stream().map(Request::getDay).distinct().map(Day::getDate).toList());
        if (checkIfTodayRequestIsActive(studentId))
        {
            daysWithActiveRequests.add(LocalDate.now());
        }
        return daysWithActiveRequests;
    }

    public void changePresenceOfStudent(Long studentId, LocalDate date, Boolean hasCome)
    {
        RequestStatus oldStatus = showStatusByStudentIdAndDate(studentId, date);
        if (hasCome)
        {
            requestRepository.updateStatusByDate(studentId, date, requestStatusRepository.findByStatus("HAS_COME").get());
            //статусы есть: "не просмотрено" (по умолчанию); "просмотрено" (не выводить в списке заявок);
            //"пришел"; "не пришел" (уже после занятия, когда заполняется посещаемость)
            studentService.changeAttendedClasses(studentId, true); //при посещении надо изменить число посещенных занятий
            //если прибавить число посещенных, то уменьшится число доступных, и, значит, надо проверить, что заявок на следующие дни не больше, чем доступно,
            //и удалить самые поздние, если больше
            List<LocalDate> daysWithActiveRequests = findDaysWithActiveRequests(studentId);
            int availableClasses = 0;
            try
            {
                availableClasses = purchaseHistoryService.findPurchaseWithActiveSeasonTicket(studentId, date).getAvailableClasses();
            }
            catch (PurchaseNotFoundException ignored)
            {}
            while (daysWithActiveRequests.size() > availableClasses)
            {
                LocalDate maxDate = daysWithActiveRequests.get(0);
                for (LocalDate localDate: daysWithActiveRequests)
                {
                    if (localDate.isAfter(maxDate))
                    {
                        maxDate = localDate;
                    }
                }
                requestRepository.removeByStudentIdAndDayDate(studentId, maxDate);
                daysWithActiveRequests = findDaysWithActiveRequests(studentId);
            }
        }
        else
        {
            requestRepository.updateStatusByDate(studentId, date, requestStatusRepository.findByStatus("HAS_NOT_COME").get());
            if (oldStatus.getStatus().equals("HAS_COME")) //если старый статус был "пришел", то он изменяется в отсутствие и надо уменьшить число занятий
            {
                studentService.changeAttendedClasses(studentId, false);
            }
        }
    }



















    public List<String> showShortInfoAboutSession(LocalDate date, LocalTime timeStart)
    {
        int numberOfJuniors = 0; //блок вывода информации о посещаемости
        int numberOfMiddles = 0;
        int numberOfSeniors = 0;
        List<Request> requests = requestRepository.findIfIntersectByTime(date, timeStart, timeStart.plusMinutes(29));
        for (Request request: requests)
        {
            switch (request.getStudent().getRank_name().getRank_name())
            {
                case "juniors" -> ++numberOfJuniors;
                case "middles" -> ++numberOfMiddles;
                default -> ++numberOfSeniors;
            }
        }
        int numberOfOccupiedShiels = numberOfJuniors + numberOfMiddles / 2 + numberOfMiddles % 2 + numberOfSeniors / 2 + numberOfSeniors % 2;
        List<String> info = new ArrayList<>();
        info.add(Integer.toString(numberOfJuniors));
        info.add(Integer.toString(numberOfMiddles));
        info.add(Integer.toString(numberOfSeniors));
        info.add(Integer.toString(numberOfOccupiedShiels));
        return info;
    }

    public  List<String> showInfoAboutSession(Long studentId, LocalDate date, LocalTime timeStart)
    {
        List<String> info = showShortInfoAboutSession(date, timeStart);
        if (LocalDate.now().isAfter(date) || (LocalDate.now().isEqual(date) && (LocalTime.now().isAfter(timeStart))))
        {
            info.add("Редактирование заявки недоступно: занятие уже началось или прошло"); //проверка на актуальность времени
            return info;
        }

        Optional<Request> optionalRequest = requestRepository.findIfAPartOfStudentRequest(studentId, date, timeStart, timeStart.plusMinutes(29));
        if (optionalRequest.isPresent()) //проверка, записан(а) ли уже
        {
            LocalTime timeStartFoRemoving = optionalRequest.get().getTimeStart();
            if (date.isEqual(LocalDate.now()) && timeStartFoRemoving.isBefore(LocalTime.now()))
            {
                info.add("Редактирование заявки недоступно: занятие уже началось или прошло");
                return info;
            }
            info.add("Отменить заявку");
            info.add(timeStartFoRemoving.toString());
            return info;
        }

        LocalTime timeDuration; //проверка, можно ли еще записаться (связано с билетом и заявками на сегодня)
        int availableClasses = 0;
        try
        {
            PurchaseHistory purchase = purchaseHistoryService.findPurchaseWithActiveSeasonTicket(studentId, date);
            timeDuration = purchase.getSeasonTicket().getTimeDuration();
            if (findDaysWithActiveRequests(studentId).size() >= purchase.getAvailableClasses())
            {
                info.add("Число записей на будущие занятия достигло лимита");
                return info;
            }
        }
        catch (PurchaseNotFoundException e)
        {
            if (purchaseHistoryService.existByStudentId(studentId))
            {
                info.add("Нет активного абонемента на момент этой даты");
                return info;
            }
            timeDuration = lenghtOfMasterClass;
        }
        if (!timeDuration.equals(LocalTime.of(23, 59)))
        {
            if (requestRepository.existsByStudentIdAndDayDate(studentId, date))
            {
                info.add("Вы уже записались на сегодня");
                return info;
            }
        }

        Student student = studentService.findStudentById(studentId); //проверка на число новичков и пр.
        LocalTime localStart = timeStart;
        LocalTime localEnd = timeStart;
        LocalTime endOfRequest;
        if (timeDuration.equals(LocalTime.of(23, 59)))
        {
            endOfRequest = timeStart.plusMinutes(29L);
        }
        else
        {
            endOfRequest = timeStart.plusHours(timeDuration.getHour()).plusMinutes(timeDuration.getMinute() - 1);
        }

        while (localEnd.isBefore(endOfRequest))
        {
            localEnd = localEnd.plusMinutes(29L);
            if (localEnd.isAfter(endOfRequest))
            {
                localEnd = endOfRequest;
            }
            int localNumberOfJuniors = 0;
            int localNumberOfMiddles = 0;
            int localNumberOfSeniors = 0;
            switch (student.getRank_name().getRank_name())
            {
                case "juniors" -> ++localNumberOfJuniors;
                case "middles" -> ++localNumberOfMiddles;
                default -> ++localNumberOfSeniors;
            }
            List<Request> localRequests = requestRepository.findIfIntersectByTime(date, localStart, localEnd);
            for (Request request: localRequests)
            {
                switch (request.getStudent().getRank_name().getRank_name())
                {
                    case "juniors" -> ++localNumberOfJuniors;
                    case "middles" -> ++localNumberOfMiddles;
                    default -> ++localNumberOfSeniors;
                }
            }
            if (localNumberOfJuniors > wishedNumberOfJuniors)
            {
                info.add("Нельзя записаться из-за числа большого новичков в каком-то из получасов");
                return info;
            }
            if (localNumberOfJuniors + localNumberOfMiddles > wishedNumberOfDemandingTrainer)
            {
                info.add("Нельзя записаться из-за большого числа требующих тренера в каком-то из получасов");
                return info;
            }
            if (localNumberOfJuniors + localNumberOfMiddles / 2 + localNumberOfMiddles % 2 + localNumberOfSeniors / 2 + localNumberOfSeniors % 2 > numberOfShields)
            {
                info.add("Нельзя записаться из-за большого числа занятых щитов в каком-то из получасов");
                return info;
            }
            localEnd = localEnd.plusMinutes(1L);
            localStart = localStart.plusMinutes(30L);
        }
        info.add("Записаться");
        info.add(endOfRequest.toString());
        return info;
    }


    public void addRequest(Long studentId, LocalDate date, LocalTime timeStart, LocalTime timeEnd)
    {
        Day day = dayService.findByDate(date);
        Student student = studentService.findStudentById(studentId);
        Request request = new Request();
        request.setDay(day);
        request.setStudent(student);
        request.setTimeStart(timeStart);
        request.setTimeEnd(timeEnd);
        requestRepository.save(request);
    }

    public void removeByStudentIdAndDateTimeStart(Long studentId, LocalDate date, LocalTime timeStart)
    {
        requestRepository.removeByStudentIdAndDayDateAndTimeStart(studentId, date, timeStart);
    }






















    /*
    public void removeByDate(LocalDate date)
    {
        requestRepository.removeByDayDate(date);
    }

    public void removeActiveRequestsByStudent(Long studentId, LocalDate date, LocalTime time)
    {
        requestRepository.removeActiveRequestsByStudentId(studentId, date, time);
    }*/
}
