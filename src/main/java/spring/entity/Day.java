package spring.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.sql.Time;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "days")
public class Day {
    @Id
    @Column(name = "date", nullable = false)
    private Date date;
    @Basic
    @Column(name = "time_start")
    private Time timeStart;
    @Basic
    @Column(name = "time_end")
    private Time timeEnd;
    @Basic
    @Column(name = "are_lessons")
    private Boolean areLessons;
}
