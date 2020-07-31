package eve.tools.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Entity
@Data
public class Role implements GrantedAuthority {

	private static final long serialVersionUID = 1L;

	@Id
	private String role;

	public Role() {
	}

	public Role(String role) {
		this.role = role;
	}

	@Override
	public String getAuthority() {
		return role;
	}
}
