package spring.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "request_status")
public class RequestStatus {
    @Id
    @Column(name = "status")
    private String status;
}
