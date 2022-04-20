package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.entity.Event;

import javax.transaction.Transactional;
import java.util.Date;

@Repository
public interface EventRepository extends JpaRepository<Event, String>
{
    void removeByEventId(Long id);
}
