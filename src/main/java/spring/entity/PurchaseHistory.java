package spring.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "purchase_history")
public class PurchaseHistory {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "purchase_id")
    private Long purchaseId;

    @Basic
    @Column(name = "start_date")
    private Date startDate;

    @ManyToOne
    @JoinColumn(name="student_id", referencedColumnName="student_id")
    @JsonManagedReference
    private Student student;

    @ManyToOne
    @JoinColumn(name="ticket_type", referencedColumnName="ticket_type")
    @JsonManagedReference
    private SeasonTicket seasonTicket;

    @Basic
    @Column(name = "available_classes")
    private Integer availableClasses;
}
