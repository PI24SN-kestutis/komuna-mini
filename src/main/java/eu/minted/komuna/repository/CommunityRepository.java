package eu.minted.komuna.repository;

import eu.minted.komuna.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    @NonNull
    Optional<Community> findById(@NonNull Long id);

    Optional<Community> findByCode(String code);
    Optional<Community> findByName(String name);
}
