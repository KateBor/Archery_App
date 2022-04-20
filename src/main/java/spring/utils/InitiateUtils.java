package spring.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import spring.entity.ProfileStatus;
import spring.entity.Rank;
import spring.entity.Student;
import spring.repositories.ProfileStatusRepository;
import spring.repositories.RankRepository;
import spring.repositories.StudentRepository;
import spring.requests.RegisterRequest;
import spring.service.ProfileStatusServiceImpl;
import spring.service.StudentServiceImpl;

@Service
@RequiredArgsConstructor
@Component
public class InitiateUtils implements CommandLineRunner {

    @Autowired
    private final StudentServiceImpl studentService;
    @Autowired
    private final ProfileStatusServiceImpl profileStatusService;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final RankRepository rankRepository;
    @Autowired
    private final ProfileStatusRepository profileStatusRepository;

    @Override
    public void run(String... args) throws Exception {
//
//        profileStatusRepository.save(new ProfileStatus("onChecking"));
//        profileStatusRepository.save(new ProfileStatus("approved"));
//        profileStatusRepository.save(new ProfileStatus("deleted"));
//        profileStatusRepository.save(new ProfileStatus("notRegistered"));
//
//        rankRepository.deleteAll();
//
                System.out.println("список статусов");
                for(ProfileStatus ps : profileStatusService.getStatusList()) {
                    System.out.println(ps.toString());
                }
//
//        System.out.println("пытаемся зарегистрироваться");
//        studentRepository.deleteAll();
//        studentRepository.save(studentService.createStudent(new RegisterRequest("Kate", "Borisova", "89171124120", "kate_boriso2002@mail.ru", "04.04.2002", "pwd")));
////
        for (Student st :studentService.getAllStudents()) {
            System.out.println(st.toString());
        }
    }
}
