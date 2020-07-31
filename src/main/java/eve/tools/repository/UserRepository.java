package eve.tools.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import eve.tools.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
