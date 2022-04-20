package spring.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "season_tickets")
public class SeasonTicket {
    @Id
    @Column(name = "ticket_type")
    private String ticketType;
    @Basic
    @Column(name = "cost")
    private double cost;
    @Basic
    @Column(name = "number_of_classes")
    private int numberOfClasses;
    @Basic
    @Column(name = "time_duration")
    private Time timeDuration;
    @Basic
    @Column(name = "days_duration")
    private Integer daysDuration;
    @Basic
    @Column(name = "is_for_sale")
    private Boolean isForSale;
}
