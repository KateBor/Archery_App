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
        if (hasCome) //если надо отметить посещение:
        {
            requestRepository.updateStatusByDate(studentId, date, requestStatusRepository.findByStatus("HAS_COME").get()); //обновить статус на "пришел"
            studentService.changeAttendedClasses(studentId, true); //увеличить число посещенных занятий
            List<LocalDate> daysWithActiveRequests = findDaysWithActiveRequests(studentId); //найти все будущие дни с заявками этого студента
            int availableClasses = 0;
            try //найти покупку с активными абонементом и взять оттуда число доступных занятий:
            {
                availableClasses = purchaseHistoryService.findPurchaseWithActiveSeasonTicket(studentId, date).getAvailableClasses();
            }
            catch (PurchaseNotFoundException ignored) //если активного абонемента нет, число доступных занятий равно нулю
            {}
            while (daysWithActiveRequests.size() > availableClasses)//пока число дней с будущими заявками больше числа доступных занятий, лишние заявки удалять
            {
                LocalDate maxDate = daysWithActiveRequests.get(0);
                for (LocalDate localDate: daysWithActiveRequests)
                {
                    if (localDate.isAfter(maxDate))
                    {
                        maxDate = localDate;
                    }
                }
                requestRepository.removeByStudentIdAndDayDate(studentId, maxDate); //удалить заявки на самый далекий день
                daysWithActiveRequests = findDaysWithActiveRequests(studentId);
            }
        }
        else //если надо отметить непосещение:
        {
            requestRepository.updateStatusByDate(studentId, date, requestStatusRepository.findByStatus("HAS_NOT_COME").get());
            if (oldStatus.getStatus().equals("HAS_COME")) //если старый статус был "пришел", надо уменьшить число посещенных занятий, т.к. оно было увеличено:
            {
                studentService.changeAttendedClasses(studentId, false);
            }
        }
    }



















    //в параметрах дата и время этого получаса, например, 20 июня 10:00 (до 10:29):
    public List<String> showShortInfoAboutSession(LocalDate date, LocalTime timeStart)
    {
        int numberOfJuniors = 0;
        int numberOfMiddles = 0;
        int numberOfSeniors = 0;
        //найти все заявки, которые перескаются с временем этого получаса:
        List<Request> requests = requestRepository.findIfIntersectByTime(date, timeStart, timeStart.plusMinutes(29));
        for (Request request: requests)
        {
            //в каждой заявке находим студента, смотрим его ранг и увеличиваем число людей этого ранга:
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
        //проверка на актуальность времени:
        if (LocalDate.now().isAfter(date) || (LocalDate.now().isEqual(date) && (LocalTime.now().isAfter(timeStart))))
        {
            info.add("Редактирование заявки недоступно: занятие уже началось или прошло");
            return info;
        }

        //найти, имеется ли у студента заявка, во времени которой есть этот получас:
        Optional<Request> optionalRequest = requestRepository.findIfAPartOfStudentRequest(studentId, date, timeStart, timeStart.plusMinutes(29));
        if (optionalRequest.isPresent())
        {
            //найти время начала этой заявки и сравнить с текущим моментом:
            LocalTime timeStartOfRequest = optionalRequest.get().getTimeStart();
            if (date.isEqual(LocalDate.now()) && timeStartOfRequest.isBefore(LocalTime.now()))
            {
                info.add("Редактирование заявки недоступно: занятие уже началось или прошло");
                return info;
            }
            //если ещё не поздно, то есть возможность отменить заявку:
            info.add("Отменить заявку");
            info.add(timeStartOfRequest.toString());
            return info;
        }

        //если заявки на это время нет, надо проверить, может ли человек записаться:
        LocalTime timeDuration;
        try
        {
            //ищем покупку с активным абонементом и находим длину занятия в этом абонементе (понадобится позже):
            PurchaseHistory purchase = purchaseHistoryService.findPurchaseWithActiveSeasonTicket(studentId, date);
            timeDuration = purchase.getSeasonTicket().getTimeDuration();
            //ищем уже имеющиеся заявки на будущее время и сравниваем с числом доступных занятий по абонементу; если заявок слишком много,
            //записаться уже нельзя:
            if (findDaysWithActiveRequests(studentId).size() >= purchase.getAvailableClasses())
            {
                info.add("Число записей на будущие занятия достигло лимита");
                return info;
            }
        }
        //если покупки с активным абонементом нет, выскакивает исключение:
        catch (PurchaseNotFoundException e)
        {
            //если у человека никогда не было покупок, ему можно предложить мастер-класс как полному новичку, иначе он может лишь продлить абонемент:
            if (purchaseHistoryService.existByStudentId(studentId))
            {
                info.add("Нет активного абонемента на момент этой даты");
                return info;
            }
            timeDuration = lenghtOfMasterClass;
        }
        //итак, вычислили длину занятия (это либо длина мастер-класса, либо длина из активного абонемента); теперь надо проверить, не является ли абонемент
        //безлимитным (в таком случае длина равна 23:59, то есть заниматься можно сколько угодно):
        if (!timeDuration.equals(LocalTime.of(23, 59)))
        {
            //если абонемент НЕ безлимитный, а на сегодня уже есть заявки - записаться больше нельзя:
            if (requestRepository.existsByStudentIdAndDayDate(studentId, date))
            {
                info.add("Вы уже записались на сегодня");
                return info;
            }
        }

        //если мы дошли сюда - значит, человек по-любому может записаться на занятие и ничего ему не мешает; осталось проверить, может ли он
        //записаться из-за людей, или в каком-то из получасов из его заявки слишком много записавшихся; для этого в цикле надо пройтись по всем получасам:
        Student student = studentService.findStudentById(studentId);
        //начало очередного получаса:
        LocalTime localStart = timeStart;
        //конец очередного получаса:
        LocalTime localEnd = timeStart;
        //конец всей заявки:
        LocalTime endOfRequest;
        //если абонемент безлимитный, заявка будет длиной в полчаса, но человек может записаться сколько угодно, иначе - заявка равна длине
        //занятия в абонементе:
        if (timeDuration.equals(LocalTime.of(23, 59)))
        {
            endOfRequest = timeStart.plusMinutes(29L);
        }
        else
        {
            endOfRequest = timeStart.plusHours(timeDuration.getHour()).plusMinutes(timeDuration.getMinute() - 1);
        }
        //пока конец очередного получаса не достигнет конца заявки, выполняется цикл:
        while (localEnd.isBefore(endOfRequest))
        {
            localEnd = localEnd.plusMinutes(29L);
            //если при прибавлении получаса конец очередного получаса окажется позже конца заявки, то осталось меньше получаса (например, 20 минут),
            //и тогда конец очередного "получаса" приравнивается к концу заявки:
            if (localEnd.isAfter(endOfRequest))
            {
                localEnd = endOfRequest;
            }
            int localNumberOfJuniors = 0;
            int localNumberOfMiddles = 0;
            int localNumberOfSeniors = 0;
            //в зависимости от ранга текущего студента увеличивается та или иная переменная по подсчету новичков и прочих:
            switch (student.getRank_name().getRank_name())
            {
                case "juniors" -> ++localNumberOfJuniors;
                case "middles" -> ++localNumberOfMiddles;
                default -> ++localNumberOfSeniors;
            }
            //проверим все остальные заявки на это время и ранги их учеников:
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
            //теперь проверка на желаемое число новичков, желаемое число середнячков и число занятых щитов:
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
            if (localNumberOfJuniors + localNumberOfMiddles / 2 + localNumberOfMiddles % 2 + localNumberOfSeniors / 2 + localNumberOfSeniors % 2 >
                    numberOfShields)
            {
                info.add("Нельзя записаться из-за большого числа занятых щитов в каком-то из получасов");
                return info;
            }
            localEnd = localEnd.plusMinutes(1L);
            localStart = localStart.plusMinutes(30L);
        }
        //если всё нормально, можно записаться:
        info.add("Записаться");
        info.add(endOfRequest.toString());
        return info;
    }

    //добавить заявку
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

    //удалить заявку
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
