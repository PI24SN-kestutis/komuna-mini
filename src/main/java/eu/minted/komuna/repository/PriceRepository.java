package eu.minted.komuna.repository;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Fee;
import eu.minted.komuna.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    List<Price> findByFee(Fee fee);

    List<Price> findByValidFromBeforeAndValidToAfter(LocalDate start, LocalDate end);

    List<Price> findByCommunity(Community community);
}
