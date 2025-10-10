package eu.minted.komuna.repository;

import eu.minted.komuna.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {


    @Query("SELECT c FROM Community c WHERE c.code = :code")
    Optional<Community> findByCode(@Param("code") String code);

    @Query(value = "SELECT * FROM communities WHERE LOWER(name) LIKE LOWER(CONCAT('%', :term, '%')) OR code LIKE CONCAT('%', :term, '%')", nativeQuery = true)
    List<Community> searchCommunities(@Param("term") String term);

}
