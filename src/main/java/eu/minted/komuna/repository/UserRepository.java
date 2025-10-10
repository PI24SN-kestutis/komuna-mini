package eu.minted.komuna.repository;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.community = :community AND u.role.name = 'RESIDENT'")
    List<User> findByCommunityAndRoleResident(@Param("community") Community community);

    @Query("SELECT COUNT(u) FROM User u WHERE u.community = :community AND u.role.name = 'RESIDENT'")
    long countResidentsByCommunity(@Param("community") Community community);

    @Query("SELECT u FROM User u WHERE u.community = :community AND u.role.name = :roleName")
    List<User> findByCommunityAndRoleName(@Param("community") Community community, @Param("roleName") String roleName);
}
