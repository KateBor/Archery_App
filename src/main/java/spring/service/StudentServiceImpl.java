package spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.entity.ProfileStatus;
import spring.entity.Rank;
import spring.entity.Student;
import spring.exception.AlreadyExistException;
import spring.exception.InvalidEnterValueException;
import spring.exception.StudentNotFoundException;
import spring.repositories.ProfileStatusRepository;
import spring.repositories.RankRepository;
import spring.repositories.StudentRepository;
import spring.requests.RegisterRequest;
import spring.utils.Constants;

import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    private final ProfileStatusRepository profileStatusRepository;

    private final RankRepository rankRepository;

    private final ProfileStatusService profileStatusService;

    private final PasswordEncoder pwdEncoder;

    private final PurchaseHistoryService purchaseHistoryService;

    private final RankService rankService;


    @Override
    public Student createStudent(RegisterRequest request) {
        Student student = new Student();
        student.setFirst_name(request.getFirst_name());
        student.setLast_name(request.getLast_name());

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

        String email = request.getEmail();
        regex = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}";
        if (email.matches(regex)) {
            student.setEmail(email); //проверить на корректность (и на существование такого?)
        }                           //разобраться как отправлять письмо на почту с подтверждением

        student.setProfile_status(profileStatusService.findByProfileStatus(Constants.ProfileStatusConst.ON_CHECKING));
        student.setRank_name(rankService.findByRankName(Constants.RankConst.NO_RANK));
        student.setRoles(Collections.singletonList("ROLE_USER"));
        student.setPassword_hash(pwdEncoder.encode(request.getPassword_hash()));

        String dateString = request.getBirth_date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            throw new InvalidEnterValueException("Invalid date");
        }
        Date currentDate = new Date();
        if (date.after(currentDate)) {
            throw new InvalidEnterValueException("Invalid date");
        }
        student.setBirth_date(date);

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
        if (student.isPresent()) {
            return student.get();
        }
        throw new StudentNotFoundException("Student not found");
    }

    @Override
    public Student findStudentByPhoneNumber(String phone) {
        Optional<Student> student = studentRepository.findUserByPhone_number(phone);
        if (student.isPresent()) {
            return student.get();
        }
        throw new StudentNotFoundException("Student not found");
    }

    @Override
    public Student findStudentByToken(String token) {
        Optional<Student> optionalStudent = studentRepository.findStudentByToken(token);
        if (optionalStudent.isPresent()) {
            return optionalStudent.get();
        }
        throw new StudentNotFoundException("Student not found");
    }

    @Override
    @Transactional
    public void updateFirstName(long student_id, String firstName) {
        Optional<Student> optionalStudent = studentRepository.findById(student_id);
        if (optionalStudent.isPresent()) {
            studentRepository.updateFirstName(student_id, firstName);
            return;
        }
        throw new NoSuchElementException("Invalid student_id");
    }

    @Override
    @Transactional
    public void updateLastName(long student_id, String lastName) {
        Optional<Student> optionalStudent = studentRepository.findById(student_id);
        if (optionalStudent.isPresent()) {
            studentRepository.updateLastName(student_id, lastName);
            return;
        }
        throw new NoSuchElementException("Invalid student_id");
    }

    @Override
    @Transactional
    public void updatePhoneNumber(long student_id, String phoneNumber) {
        Optional<Student> optionalStudent = studentRepository.findById(student_id);
//        Optional<Student> optionalNumber = studentRepository.findUserByPhone_number(phoneNumber);
//        if (optionalNumber.isPresent()) {
//            throw new AlreadyExistException("Account with this email already registered");
//        }
        if (optionalStudent.isPresent()) {
            studentRepository.updatePhoneNumber(student_id, phoneNumber);
            return;
        }
        throw new NoSuchElementException("Invalid student_id");
    }

    @Override
    @Transactional
    public void updateEmail(long student_id, String email) {
        Optional<Student> optionalStudent = studentRepository.findById(student_id);
        Optional<Student> optionalEmail = studentRepository.findUserByEmail(email);
        if (optionalEmail.isPresent()) {
            throw new AlreadyExistException("Account with this email already registered");
        }
        if (optionalStudent.isPresent()) {
            studentRepository.updateEmail(student_id, email);
            return;
        }
        throw new StudentNotFoundException("Invalid student_id");
    }

    @Override
    @Transactional
    public void updateBirthDate(long student_id, Date birthDate) {
        Optional<Student> optionalStudent = studentRepository.findById(student_id);
        if (optionalStudent.isPresent()) {
            studentRepository.updateBirthDate(student_id, birthDate);
            return;
        }
        throw new StudentNotFoundException("Invalid student_id");
    }


    @Override
    @Transactional
    public void updateProfileStatus(long student_id, String status) {
        Optional<ProfileStatus> optionalProfileStatus = profileStatusRepository.findByStatus(status);
        Optional<Student> optionalStudent = studentRepository.findById(student_id);
        if (optionalProfileStatus.isPresent() && optionalStudent.isPresent()) {
            studentRepository.updateProfileStatus(student_id, optionalProfileStatus.get());
            return;
        }
        throw new StudentNotFoundException("Invalid student_id or profile_status");
    }

    @Override
    @Transactional
    public void updateRank(long student_id, String rank) {
        Optional<Rank> optionalRank = rankRepository.findByRank_name(rank);
        Optional<Student> optionalStudent = studentRepository.findById(student_id);
        if (optionalRank.isPresent() && optionalStudent.isPresent()) {
            studentRepository.updateRank(student_id, optionalRank.get());
            return;
        }
        throw new StudentNotFoundException("Invalid student_id or rank_name");
    }

    @Override
    @Transactional
    public void updateToken(Long id, String token) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            studentRepository.updateToken(id, token);
            return;
        }
        throw new StudentNotFoundException("Invalid student_id");
    }




    @Override
    @Transactional
    public void updateHasPaid(long student_id, Boolean hasPaid) {
        Optional<Student> optionalStudent = studentRepository.findById(student_id);
        if (optionalStudent.isPresent()) {
            studentRepository.updateHasPaid(student_id, hasPaid);
            return;
        }
        throw new StudentNotFoundException("Invalid student_id");
    }

    @Override
    public Rank getRank(long id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            return optionalStudent.get().getRank_name();
        }
        throw new StudentNotFoundException("Student not found");
    }

    @Override
    public Boolean hasPaid(long id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            return optionalStudent.get().getHasPaid();
        }
        throw new StudentNotFoundException("Student not found");
    }

    @Override
    @Transactional
    public void changeAttendedClasses(Long id, Boolean toIncrease) {
        if (toIncrease) {
            studentRepository.increaseAttendedClasses(id);
            LocalDate date = LocalDate.now();
            purchaseHistoryService.changeAvailableClassesFromActivePurchase(id, date, true); //в активном абонементе уменьшить число доступных занятий
        }
        else {
            studentRepository.decreaseAttendedClasses(id); //если админ ошиблась и хочет обратно уменьшить число посещенных занятий
            LocalDate date = LocalDate.now(); //тогда в активном абонементе надо обратно увеличить число доступных занятий
            if (!purchaseHistoryService.changeAvailableClassesFromActivePurchase(id, date, false)) //если активного абонемента не оказалось
            { //(например, сбросился, так как число доступных занятий стало равно нулю в результате ошибки, случайного нажатия),
                purchaseHistoryService.changeAvailableClassesFromLastPurchase(id, false); //то вместо него увеличить число доступных занятий в последнем купленном абонементе
            }
        }
    }



        /*@Override
    public void updateHasPaid(long student_id, Boolean hasPaid)
    {
        if (hasPaid)
        {
            studentRepository.updateHasPaid(student_id, true);
        }
        studentRepository.updateHasPaid(student_id, false);
    }*/

       /* public Boolean hasPaid(long id)
    {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent())
        {
            return optionalStudent.get().getHasPaid();
        }
        throw new StudentNotFoundException("Student not found");
    }*/
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<Student> student = studentRepository.findUserByEmail(username);
//        if (student.isEmpty()) {
//            throw new UsernameNotFoundException("Student not found");
//        }
//        return student.get();
//    }
}
