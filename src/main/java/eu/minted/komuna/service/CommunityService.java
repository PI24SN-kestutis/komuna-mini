package eu.minted.komuna.service;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.repository.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    public List<Community> findAll() {
        return communityRepository.findAll();
    }

    public Optional<Community> findById(Long id) {
        return communityRepository.findById(id);
    }

    public Optional<Community> findByCode(String code) {
        return communityRepository.findByCode(code);
    }

    public Optional<Community> findByName(String name) {
        return communityRepository.findByName(name);
    }

    public void save(Community community) {
        communityRepository.save(community);
    }

    public void deleteById(Long id) {
        communityRepository.deleteById(id);
    }
}
