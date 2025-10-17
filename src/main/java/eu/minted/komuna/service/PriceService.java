package eu.minted.komuna.service;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Fee;
import eu.minted.komuna.model.Price;
import eu.minted.komuna.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    public List<Price> findAll() {
        return priceRepository.findAll();
    }

    public Optional<Price> findById(Long id) {
        return priceRepository.findById(id);
    }

    public List<Price> findByFee(Fee fee) {
        return priceRepository.findByFee(fee);
    }

    public List<Price> findActiveBetween(LocalDate start, LocalDate end) {
        return priceRepository.findByValidFromBeforeAndValidToAfter(start, end);
    }

    public Price save(Price price) {
        return priceRepository.save(price);
    }

    public void deleteById(Long id) {
        priceRepository.deleteById(id);
    }

    public List<Price> findByCommunity(Community community) {
        return priceRepository.findByCommunity(community);
    }

}
