package eu.minted.komuna.repository;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    @Query("SELECT f FROM Fee f WHERE f.user.community = :community")
    List<Fee> findByUser_Community(@Param("community") Community community);

    @Query("SELECT COUNT(f) FROM Fee f WHERE f.user.community = :community")
    long countByUser_Community(@Param("community") Community community);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Fee f WHERE f.user.community = :community AND f.paid = true")
    double sumAmountByUser_CommunityAndPaidTrue(@Param("community") Community community);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Fee f WHERE f.user.community = :community AND f.paid = false")
    double sumAmountByUser_CommunityAndPaidFalse(@Param("community") Community community);

    /** Statistinei ataskaitai – visų gyventojų ir jų mokesčių suvestinė */
    @Query("""
           SELECT f 
           FROM Fee f
           JOIN FETCH f.user u
           WHERE u.community = :community
           ORDER BY u.name ASC, f.type ASC
           """)
    List<Fee> findDetailedByCommunity(@Param("community") Community community);

    @Query("SELECT f FROM Fee f WHERE f.user.id = :userId ORDER BY f.type ASC")
    List<Fee> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(f.amount),0) FROM Fee f WHERE f.user.id = :userId")
    double sumByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(f.amount),0) FROM Fee f WHERE f.user.id = :userId AND f.paid = true")
    double sumPaidByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(f.amount),0) FROM Fee f WHERE f.user.id = :userId AND f.paid = false")
    double sumUnpaidByUserId(@Param("userId") Long userId);
}
