package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.entity.ProfileStatus;
import spring.entity.Rank;
import spring.entity.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("select s from Student s where s.email = ?1")
    Optional<Student> findUserByEmail(String email);

    @Query("select s from Student s where s.phone_number = ?1")
    Optional<Student> findUserByPhone_number(String phone_number);

    @Query("select s from Student s where s.profile_status.status = ?1")
    List<Student> findStudentsByProfileStatus(String status);

    @Modifying
    @Query("update Student s set s.profile_status = ?2 where s.id = ?1")
    void updateProfileStatus(long id, ProfileStatus status);

    @Modifying
    @Query("update Student s set s.rank_name = ?2 where s.id = ?1")
    void updateRank(long id, Rank rank);

    @Modifying
    @Query("update Student s set s.hasPaid = ?2 where s.id = ?1")
    void updateHasPaid(long id, Boolean hasPaid);

    @Query("update Student s set s.token = ?2 where s.id = ?1")
    void updateToken(Long id, String token);

    @Modifying
    @Query("update Student s set s.attended_classes = s.attended_classes + 1 where s.id = :id")
    void increaseAttendedClasses(long id);

    @Modifying
    @Query("update Student s set s.attended_classes = s.attended_classes - 1 where s.id = :id and s.attended_classes > 0")
    void decreaseAttendedClasses(long id);
}
