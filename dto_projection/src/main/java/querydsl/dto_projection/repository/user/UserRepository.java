package querydsl.dto_projection.repository.user;


import org.springframework.data.jpa.repository.JpaRepository;
import querydsl.dto_projection.entity.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);
    Optional<Users> findByName(String name);
}
