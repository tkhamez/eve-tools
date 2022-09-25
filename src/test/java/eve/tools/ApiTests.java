package eve.tools;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eve.tools.esi.Api;
import eve.tools.esi.model.universe.UniverseName;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class ApiTests {

	@Autowired
    private Api api;

	@Test
	public void universeNames() {
		List<Long> ids = new ArrayList<>();
		ids.add((long) 96061222); // character

		List<UniverseName> actual = api.universeNames(ids);

		List<UniverseName> expected = new ArrayList<>();
		expected.add(new UniverseName((long) 96061222, "Tian Khamez", "character"));

		Assertions.assertEquals(expected, actual);
	}
}
