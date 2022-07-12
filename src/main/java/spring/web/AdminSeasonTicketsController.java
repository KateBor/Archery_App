package spring.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.entity.SeasonTicket;
import spring.service.SeasonTicketService;

import java.util.List;

@RestController
@RequestMapping("/archery/admin/tickets")
@RequiredArgsConstructor
public class AdminSeasonTicketsController
{
    private final SeasonTicketService seasonTicketService;

    @GetMapping("")
    public ResponseEntity<List<SeasonTicket>> showTickets()
    {
        return new ResponseEntity<>(seasonTicketService.findTickets(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addTicket(@RequestBody SeasonTicket seasonTicket)
    {
        seasonTicketService.addSeasonTicket(seasonTicket);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/edit")
    public ResponseEntity<String> changeIfIsForSale(@RequestBody SeasonTicket seasonTicket)
    {
        seasonTicketService.changeIfIsForSale(seasonTicket);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeTicket(@RequestBody SeasonTicket seasonTicket)
    {
        boolean canRemove = seasonTicketService.removeTicket(seasonTicket);
        if (canRemove)
        {
            return new ResponseEntity<>("Удаление успешно", HttpStatus.OK);
        }
        return new ResponseEntity<>("Удалить нельзя: билет уже использовался", HttpStatus.OK);
    }
}
