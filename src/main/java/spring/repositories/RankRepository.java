package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.entity.Rank;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<Rank, String> {
    @Query("select r from Rank r where r.rank_name = ?1")
    Optional<Rank> findByRank_name(String name);
    List<Rank> findAll();
    @Transactional
    @Modifying
    @Query("update Rank r set r.color = :color where r.rank_name = :name")
    void changeColor(String color, String name);
}
