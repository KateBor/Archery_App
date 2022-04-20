package spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.entity.ProfileStatus;
import spring.entity.Rank;
import spring.entity.Student;
import spring.exception.InvalidEnterValueException;
import spring.exception.StudentNotFoundException;
import spring.repositories.ProfileStatusRepository;
import spring.repositories.RankRepository;
import spring.repositories.StudentRepository;
import spring.requests.RegisterRequest;
import spring.utils.ProfileStatusConstants;

import org.springframework.transaction.annotation.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProfileStatusRepository profileStatusRepository;
    @Autowired
    private RankRepository rankRepository;
    @Autowired
    private ProfileStatusService profileStatusService;
    @Autowired
    private PasswordEncoder pwdEncoder;
    @Autowired
    private PurchaseHistoryService purchaseHistoryService;

    @Override
    public Student createStudent(RegisterRequest request) {
        Student student = new Student();
        student.setFirst_name(request.getFirst_name());
        student.setLast_name(request.getLast_name());

        //обработка телефона
        String phone = request.getPhone_number();
        String regex = "[0-9]+";
        if (phone.startsWith("+") && phone.length() == 12) {
            String ph = phone.substring(1);
            if (ph.matches(regex)) {
                student.setPhone_number(phone);
            } else {
                throw new InvalidEnterValueException("Invalid phone number");
            }
        } else if (phone.length() == 11 && phone.matches(regex)) {
            student.setPhone_number(phone);
        } else {
            throw new InvalidEnterValueException("Invalid phone number");
        }

        //обработка почты
        String email = request.getEmail();
        regex = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}";
        if (email.matches(regex)) {
            student.setEmail(email); //проверить на корректность (и на существование такого?)
        }

        student.setProfile_status(profileStatusService.findByProfileStatus(ProfileStatusConstants.ON_CHECKING));
        student.setPassword_hash(pwdEncoder.encode(request.getPassword_hash()));

        //обработка даты
        String dateString = request.getBirth_date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            throw new InvalidEnterValueException("Invalid date");
        }
        Date currentDate = new Date();
        if (date.after(currentDate)) { //проверка что дата рождения раньше текущего дня
            throw new InvalidEnterValueException("Invalid date");
        }
        student.setBirth_date(date); //Тип дата

        return student;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getStudentsByStatus(String status) {
        return studentRepository.findStudentsByProfileStatus(status);
    }

    @Override
    public Student findStudentById(long id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            return optionalStudent.get();
        }
        throw new StudentNotFoundException("Student not found");
    }

    @Override
    public Student findStudentByEmail(String email) {
        Optional<Student> student = studentRepository.findUserByEmail(email);
        return student.orElse(null);
    }

    @Override
    public Student findStudentByPhoneNumber(String phone) {
        Optional<Student> student = studentRepository.findUserByPhone_number(phone);
        return student.orElse(null);
    }


    @Override
    public void updateProfileStatus(long student_id, String status) {
        Optional<ProfileStatus> optionalProfileStatus = profileStatusRepository.findByStatus(status);
        Optional<Student> optionalStudent = studentRepository.findById(student_id);
        if (optionalProfileStatus.isPresent() && optionalStudent.isPresent()) {
            studentRepository.updateProfileStatus(student_id, optionalProfileStatus.get());
        }
        throw new NoSuchElementException("Invalid student_id or profile_status");
    }

    @Override
    public void updateRank(long student_id, String rank) {
        Optional<Rank> optionalRank = rankRepository.findByRank_name(rank);
        Optional<Student> optionalStudent = studentRepository.findById(student_id);
        if (optionalRank.isPresent() && optionalStudent.isPresent()) {
            studentRepository.updateRank(student_id, optionalRank.get());
        }
        throw new NoSuchElementException("Invalid student_id or rank_name");
    }

    @Override
    @Transactional
    public void updateToken(Long id, String token) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            studentRepository.updateToken(id, token);
        }
    }

    @Override
    public void updateHasPaid(long student_id, Boolean hasPaid)
    {
        if (hasPaid)
        {
            studentRepository.updateHasPaid(student_id, true);
        }
        studentRepository.updateHasPaid(student_id, false);
    }

    public Rank getRank(long id)
    {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent())
        {
            return optionalStudent.get().getRank_name();
        }
        throw new StudentNotFoundException("Student not found");
    }

    public Boolean hasPaid(long id)
    {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent())
        {
            return optionalStudent.get().getHasPaid();
        }
        throw new StudentNotFoundException("Student not found");
    }

    public void changeAttendedClasses(Long id, Boolean toIncrease)
    {
        if (toIncrease)
        {
            studentRepository.increaseAttendedClasses(id);
            Date date = new Date(); //пока так, потом разбираться с датами и взять актуальную
            purchaseHistoryService.changeAvailableClassesFromActivePurchase(id, date, true); //в активном абонементе уменьшить
        } //число доступных занятий
        else
        {
            studentRepository.decreaseAttendedClasses(id); //если админ ошиблась и хочет обратно уменьшить число посещенных занятий
            Date date = new Date(); //тогда в активном абонементе надо обратно увеличить число доступных занятий
            if (!purchaseHistoryService.changeAvailableClassesFromActivePurchase(id, date, false)) //если активного абонемента не оказалось
            { //(например, сбросился, так как число доступных занятий стало равно нулю в результате ошибки, случайного нажатия),
                purchaseHistoryService.changeAvailableClassesFromLastPurchase(id, false); //то вместо него увеличить число доступных занятий в последнем купленном абонементе
                studentRepository.updateHasPaid(id, true); //(очевидно, он и был активным)
            } //тогда нужно обновить поле "оплатил", так как при сбросе активного абонемента оно становится false, а здесь сброс откатили, снова добавив
        } //доступные занятия в последнем купленном абонементе
    }
}
