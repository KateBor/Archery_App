package spring.service;

import spring.entity.Rank;
import spring.entity.Student;
import spring.requests.RegisterRequest;

import java.util.List;

public interface StudentService {

    Student createStudent(RegisterRequest request);
    List<Student> getAllStudents();
    List<Student> getStudentsByStatus(String status);
    Student findStudentById(long id);
    Student findStudentByEmail(String email);
    Student findStudentByPhoneNumber(String phone);
    void updateProfileStatus(long student_id, String status);
    void updateRank(long student_id, String rank);
    void updateHasPaid(long student_id, Boolean hasPaid);
    Rank getRank(long id);
    Boolean hasPaid(long id);
    void updateToken(Long id, String token);
    void changeAttendedClasses(Long id, Boolean toIncrease);
}
