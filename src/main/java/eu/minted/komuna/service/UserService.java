package eu.minted.komuna.service;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Role;
import eu.minted.komuna.model.User;
import eu.minted.komuna.repository.CommunityRepository;
import eu.minted.komuna.repository.RoleRepository;
import eu.minted.komuna.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CommunityRepository communityRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Role findRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Rolė nerasta: " + name));
    }


    public Community findCommunityByCode(String code) {
        return communityRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Bendrija nerasta pagal kodą: " + code));
    }


    public User save(User user) {

        if (user.getCommunity() == null && user.getCommunityCode() != null) {
            Community community = communityRepository.findByCode(user.getCommunityCode())
                    .orElseThrow(() -> new RuntimeException("Bendrija su kodu " + user.getCommunityCode() + " nerasta"));
            user.setCommunity(community);
        }

        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    public void delete(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setCommunity(null);
            userRepository.delete(user);
        });
    }


    public List<User> findResidentsByCommunity(Community community) {
        return userRepository.findByCommunityAndRoleName(community, "RESIDENT");
    }

    public long countResidentsByCommunity(Community community) {
        return userRepository.countResidentsByCommunity(community);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
