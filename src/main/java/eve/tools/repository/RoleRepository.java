package eve.tools.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import eve.tools.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByRole(String role);
}
