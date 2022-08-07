package spring.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static spring.utils.Constants.LimitsConst.*;

@RestController
@RequestMapping("/archery/admin/edit")
@RequiredArgsConstructor
public class EditConstantsController
{
    //private final RankService rankService;

    /*@GetMapping(value = "/ranks", consumes = "application/json")
    public ResponseEntity<List<Rank>> showRanks()
    {
        return new ResponseEntity<>(rankService.findAll(), HttpStatus.OK);
    }*/

    @GetMapping(value = "/constants", consumes = "application/json")
    public ResponseEntity<List<Integer>> showConstants()
    {
        return new ResponseEntity<>(List.of(numberOfShields, wishedNumberOfJuniors, wishedNumberOfDemandingTrainer), HttpStatus.OK);
    }

    /*@PostMapping(value = "/changerankcolor", consumes = "application/json")
    public ResponseEntity<String> changeColor(@RequestParam String name, @RequestParam String color)
    {
        rankService.changeColor("blue", "juniors");
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

    @PostMapping(value = "/changeshields", consumes = "application/json")
    public ResponseEntity<String> changeShields(@RequestParam int number)
    {
        numberOfShields = number;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/changejuniors", consumes = "application/json")
    public ResponseEntity<String> changeJuniors(@RequestParam int number)
    {
        wishedNumberOfJuniors = number;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/changedemandingtrainer", consumes = "application/json")
    public ResponseEntity<String> changeDemandingTrainer(@RequestParam int number)
    {
        wishedNumberOfDemandingTrainer = number;
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
