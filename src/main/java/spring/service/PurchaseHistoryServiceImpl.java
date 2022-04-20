package spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.entity.PurchaseHistory;
import spring.entity.SeasonTicket;
import spring.entity.Student;
import spring.repositories.PurchaseHistoryRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseHistoryServiceImpl implements PurchaseHistoryService
{
    private PurchaseHistoryRepository purchaseHistoryRepository;

    public Boolean daysHavePassed(Date startDate, Date endDate, Integer daysDuration)
    {
        int days = (int)(endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
        return days > daysDuration;
    }

    public Optional<PurchaseHistory> findPurchaseWithActiveSeasonTicket(Long studentId, Date date)
    {
        return purchaseHistoryRepository.findUnspentSeasonTicket(studentId).filter(purchaseHistory ->
                !daysHavePassed(purchaseHistory.getStartDate(), date, purchaseHistory.getSeasonTicket().getDaysDuration()));
    }

    public Boolean checkActiveSeasonTicket(Long studentId, Date date)
    {
        return findPurchaseWithActiveSeasonTicket(studentId, date).isPresent();
    }

    public List<SeasonTicket> findByStudentId(Long studentId)
    {
        List<PurchaseHistory> listPurchaseHistory = purchaseHistoryRepository.findByStudentId(studentId);
        return listPurchaseHistory.stream().map(PurchaseHistory::getSeasonTicket).toList();
    }

    public PurchaseHistory addPurchase(Student student, Date startDate, SeasonTicket seasonTicket)
    {
        PurchaseHistory purchaseHistory = new PurchaseHistory();
        purchaseHistory.setStudent(student);
        purchaseHistory.setStartDate(startDate);
        purchaseHistory.setSeasonTicket(seasonTicket);
        purchaseHistory.setAvailableClasses(seasonTicket.getNumberOfClasses());
        purchaseHistoryRepository.save(purchaseHistory);
        return purchaseHistory;
    }

    public void changeAvailableClasses(PurchaseHistory purchaseHistory, Boolean toReduce)
    {
        Integer availableClasses = purchaseHistory.getAvailableClasses();
        if (toReduce)
        {
            if (availableClasses != 0)
            {
                availableClasses--;
                if (availableClasses == 0)
                {
                    purchaseHistory.getStudent().setHasPaid(false);
                }
            }
        }
        else
        {
            availableClasses++;
        }
        purchaseHistory.setAvailableClasses(availableClasses);
    }

    public Boolean changeAvailableClassesFromActivePurchase(Long studentId, Date date, Boolean toReduce)
    {
        Optional<PurchaseHistory> optionalPurchaseHistory = findPurchaseWithActiveSeasonTicket(studentId, date);
        if (optionalPurchaseHistory.isPresent())
        {
            changeAvailableClasses(optionalPurchaseHistory.get(), toReduce);
            return true;
        }
        return false;
    }

    public Boolean changeAvailableClassesFromLastPurchase(Long studentId, Boolean toReduce)
    {
        Optional<PurchaseHistory> optionalPurchaseHistory = purchaseHistoryRepository.findTopByStudentIdOrderByStartDateDesc(studentId);
        if (optionalPurchaseHistory.isPresent())
        {
            changeAvailableClasses(optionalPurchaseHistory.get(), toReduce);
            return true;
        }
        return false;
    }


    @Autowired
    public void setPurchaseHistoryRepository(PurchaseHistoryRepository purchaseHistoryRepository)
    {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }
}
