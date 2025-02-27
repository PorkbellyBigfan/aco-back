package dev.aco.back.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import dev.aco.back.Entity.User.emailAuth;

public interface MailRepository extends JpaRepository<emailAuth, Long> {
  Optional<emailAuth> getByEmail(String email);

  @Modifying
  @Query("update emailAuth as e set e.createdDateTime=:updated, e.isAuthrized=true where e.eauthId=:eauthid")
  void updateAuthTime(Long eauthid, LocalDateTime updated);
}
