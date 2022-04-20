package spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.entity.Rank;
import spring.repositories.RankRepository;

import java.util.List;

@Service
public class RankServiceImpl implements RankService
{
    private RankRepository rankRepository;

    public List<Rank> findAll()
    {
        return rankRepository.findAll();
    }

    public void changeColor(String color, String name)
    {
        rankRepository.changeColor(color, name);
    }

    @Autowired
    public void setRankRepository(RankRepository rankRepository)
    {
        this.rankRepository = rankRepository;
    }
}
