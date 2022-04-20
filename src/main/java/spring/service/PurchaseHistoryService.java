package spring.service;

import spring.entity.PurchaseHistory;
import spring.entity.SeasonTicket;
import spring.entity.Student;

import java.util.Date;
import java.util.List;

public interface PurchaseHistoryService
{
    Boolean checkActiveSeasonTicket(Long studentId, Date date);
    List<SeasonTicket> findByStudentId(Long studentId);
    PurchaseHistory addPurchase(Student student, Date startDate, SeasonTicket seasonTicket);
    Boolean changeAvailableClassesFromActivePurchase(Long studentId, Date date, Boolean toReduce);
    Boolean changeAvailableClassesFromLastPurchase(Long studentId, Boolean toReduce);
}
