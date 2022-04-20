package spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.entity.Rank;
import spring.service.RankService;

import java.util.List;

@RestController
@RequestMapping("/archery/admin/edit")
public class EditConstantsController
{
    private RankService rankService;

    @GetMapping(value = "/getRanks", consumes = "application/json")
    public ResponseEntity<List<Rank>> findAll()
    {
        return new ResponseEntity<>(rankService.findAll(), HttpStatus.OK);
    }

    @PostMapping(value = "/changeRankColor", consumes = "application/json")
    public ResponseEntity<String> changeColor(String color, String name)
    {
        rankService.changeColor("blue", "juniors");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setRankService(RankService rankService)
    {
        this.rankService = rankService;
    }
}
