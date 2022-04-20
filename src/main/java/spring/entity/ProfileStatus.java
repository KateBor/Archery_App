package spring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profile_status")
public class ProfileStatus {
    @Id
    @Column(name = "status", nullable = false)
    private String status;

    @Override
    public String toString() {
        return "ProfileStatus{" +
                "status='" + status + '\'' +
                '}';
    }
}
