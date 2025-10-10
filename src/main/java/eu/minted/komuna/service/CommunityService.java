package eu.minted.komuna.service;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.repository.CommunityRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;

    public CommunityService(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    public List<Community> findAll() {
        return communityRepository.findAll();
    }

    public Community findById(Long id) {
        return communityRepository.findById(id).orElse(null);
    }

    public Community save(Community community) {
        return communityRepository.save(community);
    }

    public void delete(Long id) {
        communityRepository.deleteById(id);
    }
}
