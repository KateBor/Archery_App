package spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.entity.Event;
import spring.repositories.EventRepository;

import java.util.Date;

@Service
public class EventServiceImpl implements EventService
{
    private EventRepository eventRepository;

    public Event addEvent(Date date, String htmlText)
    {
        Event event = new Event();
        event.setDate(date);
        event.setHtmlText(htmlText);
        return eventRepository.save(event);
    }

    public void removeEvent(Long id)
    {
        eventRepository.removeByEventId(id);
    }

    @Autowired
    public void setEventRepository(EventRepository eventRepository)
    {
        this.eventRepository = eventRepository;
    }
}
