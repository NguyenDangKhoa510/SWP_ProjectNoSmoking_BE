package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.PasswordResetToken;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByCode(String code);
    Optional<PasswordResetToken> findByUser(User user);
    void deleteByUser(User user);
}
