package tn.Backend.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import tn.Backend.entites.Role;
import tn.Backend.entites.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);
  Optional<User>	findByEmail(String email);
  Optional<User> findUserByCin(Long cin);

  @Modifying
  @Transactional
  @Query("update User u set u.password = ?2 where u.email = ?1 ")
  void updatePassword(String email, String password);


  Optional<User> findByIdAndRole(Long id, Role role);

  List<User> findAllByRole(Role role);
}