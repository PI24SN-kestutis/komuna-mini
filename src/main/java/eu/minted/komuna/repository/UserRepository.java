package eu.minted.komuna.repository;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Role;
import eu.minted.komuna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByCommunity(Community community);
}
