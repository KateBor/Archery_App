package spring.service;

import spring.entity.Rank;

import java.util.List;

public interface RankService
{
    List<Rank> findAll();
    void changeColor(String color, String name);
}
