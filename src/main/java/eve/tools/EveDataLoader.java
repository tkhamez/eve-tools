package eve.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import eve.tools.entity.Role;
import eve.tools.repository.RoleRepository;

@Component
public class EveDataLoader implements CommandLineRunner {

	private final Logger log = LoggerFactory.getLogger(EveDataLoader.class);

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public void run(String... strings) throws Exception {

		if (roleRepository.findByRole("ROLE_EVE") == null) {
			roleRepository.save(new Role("ROLE_EVE"));
			log.info("Added ROLE_EVE");
		}
		if (roleRepository.findByRole("ROLE_EVE_CORP_CONTRACTS") == null) {
			roleRepository.save(new Role("ROLE_EVE_CORP_CONTRACTS"));
			log.info("Added ROLE_EVE_CORP_CONTRACTS");
		}
		if (roleRepository.findByRole("ROLE_EVE_MOON_EXTRACTION") == null) {
			roleRepository.save(new Role("ROLE_EVE_MOON_EXTRACTION"));
			log.info("Added ROLE_EVE_MOON_EXTRACTION");
		}
		if (roleRepository.findByRole("ROLE_EVE_ASSETS") == null) {
			roleRepository.save(new Role("ROLE_EVE_ASSETS"));
			log.info("Added ROLE_EVE_ASSETS");
		}
		if (roleRepository.findByRole("ROLE_EVE_PI") == null) {
			roleRepository.save(new Role("ROLE_EVE_PI"));
			log.info("Added ROLE_EVE_PI");
		}

		roleRepository.flush();
	}
}
