package eu.minted.komuna.repository;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    List<Fee> findByCommunity(Community community);

    List<Fee> findByNameContainingIgnoreCase(String name);
}
