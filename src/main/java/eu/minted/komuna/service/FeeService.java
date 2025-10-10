package eu.minted.komuna.service;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Fee;
import eu.minted.komuna.repository.FeeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeeService {

    private final FeeRepository feeRepository;

    public FeeService(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    public List<Fee> findAll() {
        return feeRepository.findAll();
    }

    public Fee findById(Long id) {
        return feeRepository.findById(id).orElse(null);
    }

    public Fee save(Fee fee) {
        return feeRepository.save(fee);
    }

    public void delete(Long id) {
        feeRepository.deleteById(id);
    }

    public long countByCommunity(Community community) {
        return feeRepository.countByUser_Community(community);
    }

    public double sumPaidByCommunity(Community community) {
        return feeRepository.sumAmountByUser_CommunityAndPaidTrue(community);
    }

    public double sumUnpaidByCommunity(Community community) {
        return feeRepository.sumAmountByUser_CommunityAndPaidFalse(community);
    }

    public List<Fee> findByCommunity(Community community) {
        return feeRepository.findByUser_Community(community);
    }

    public List<Fee> buildReportForCommunity(Community community) {
        return feeRepository.findDetailedByCommunity(community);
    }

    public List<Fee> findByUserId(Long userId) { return feeRepository.findByUserId(userId); }
    public double sumByUserId(Long userId) { return feeRepository.sumByUserId(userId); }
    public double sumPaidByUserId(Long userId) { return feeRepository.sumPaidByUserId(userId); }
    public double sumUnpaidByUserId(Long userId) { return feeRepository.sumUnpaidByUserId(userId); }

    public void markAsPaid(Long feeId) {
        Fee f = feeRepository.findById(feeId).orElseThrow();
        f.setPaid(true);
        feeRepository.save(f);
    }

}
