package eve.tools.entity;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Entity
@Table(name = "eve_user")
@Data
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	/**
	 * Eve character ID
	 */
	@Id
	private String username;

	@ManyToMany
	private List<Role> roles;

	private String accessToken; // it's (about) 88 chars

	private Calendar expiresOn;

	@Column(columnDefinition = "TEXT")
	private String refreshToken; // length varies, can be up to 365 chars (or even more)

	private String characterName;

	@Transient
	private List<String> scopes;

	public User() {
	}

	public User(String username) {
		this.username = username;
	}

	public void setAuthorities(List<Role> authorities) {
		this.roles = authorities;
	}

	@Override
	public List<Role> getAuthorities() {
		return roles;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
