package spring.service;

import spring.entity.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event addEvent(Date date, String htmlText);
    void removeEvent(Long id);
}
