package spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.entity.SeasonTicket;
import spring.exception.SeasonTicketNotFoundException;
import spring.service.PurchaseHistoryService;
import spring.service.SeasonTicketService;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/archery/tickets")
public class SeasonTicketsController
{
    @Autowired
    SeasonTicketService seasonTicketService;

    @GetMapping("")
    public ResponseEntity<List<SeasonTicket>> showTicketsForSale()
    {
        return new ResponseEntity<>(seasonTicketService.areForSale(), HttpStatus.OK);
    }
}
