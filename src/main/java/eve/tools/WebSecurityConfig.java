package eve.tools;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Configuration
	@Order(1)
	public static class EveConfigurationAdapter extends WebSecurityConfigurerAdapter {

		public EveConfigurationAdapter() {
			super();
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/**")
					.formLogin()
					.loginPage("/login")
					.defaultSuccessUrl("/")
					.and()
					.logout()
					.logoutUrl("/logout")
					.logoutSuccessUrl("/")
					.and()
					.exceptionHandling()
					.accessDeniedPage("/login")
			;
		}
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/").permitAll();
	}
}
