package spring.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "students")
public class Student implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "student_id", nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String first_name;

    @Column(name = "last_name", nullable = false)
    private String last_name;

    @Column(name = "phone_number", nullable = false)
    private String phone_number;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "birth_date", nullable = false)
    private Date birth_date;

    @Column(name = "attended_classes")
    private int attended_classes;

    @Column(name = "benefits")
    private boolean benefits;

    @Column(name = "password_hash", nullable = false)
    private String password_hash;

    @Column(name = "token") //??
    private String token;

    @Column(name = "token_date") //??
    private String token_date;

    @Column(name = "has_paid")
    private Boolean hasPaid;

    @ManyToOne
    @JoinColumn(name="rank_name", referencedColumnName="rank_name")
    @JsonManagedReference
    private Rank rank_name;

    @ManyToOne
    @JoinColumn(name="profile_status", referencedColumnName="status")
    @JsonManagedReference
    private ProfileStatus profile_status;

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", birth_date=" + birth_date +
                ", attended_classes=" + attended_classes +
                ", benefits=" + benefits +
                ", password_hash='" + password_hash + '\'' +
                ", token='" + token + '\'' +
                ", token_date='" + token_date + '\'' +
                ", rank=" + rank_name +
                ", profileStatus=" + profile_status +
                '}';
    }

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password_hash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
