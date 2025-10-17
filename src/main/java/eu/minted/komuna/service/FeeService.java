package eu.minted.komuna.service;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Fee;
import eu.minted.komuna.repository.FeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeeService {

    @Autowired
    private FeeRepository feeRepository;

    public List<Fee> findAll() {
        return feeRepository.findAll();
    }

    public Optional<Fee> findById(Long id) {
        return feeRepository.findById(id);
    }

    public List<Fee> findByCommunity(Community community) {
        return feeRepository.findByCommunity(community);
    }

    public List<Fee> searchByName(String namePart) {
        return feeRepository.findByNameContainingIgnoreCase(namePart);
    }

    public Fee save(Fee fee) {
        return feeRepository.save(fee);
    }

    public void deleteById(Long id) {
        feeRepository.deleteById(id);
    }
}
