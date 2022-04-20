package spring.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "events")
public class Event {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "event_id")
    private Long eventId;
    @Basic
    @Column(name = "date")
    private Date date;
    @Basic
    @Column(name = "html_text")
    private String htmlText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event events = (Event) o;
        return eventId == events.eventId && Objects.equals(date, events.date) && Objects.equals(htmlText, events.htmlText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, date, htmlText);
    }
}
