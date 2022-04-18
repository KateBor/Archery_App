package spring.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.sql.Time;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "requests")
public class Request {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "request_id")
    private Long requestId;
    @Basic
    @Column(name = "time_start")
    private Time timeStart;
    @Basic
    @Column(name = "time_end")
    private Time timeEnd;

    @ManyToOne
    @JoinColumn(name="request_status", referencedColumnName="status")
    @JsonManagedReference
    private RequestStatus status;

    @ManyToOne
    @JoinColumn(name="students", referencedColumnName="student_id")
    @JsonManagedReference
    private Student student;

    @ManyToOne
    @JoinColumn(name="days", referencedColumnName="date")
    @JsonManagedReference
    private Day day;
}
