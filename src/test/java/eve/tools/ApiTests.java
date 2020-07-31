package eve.tools;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eve.tools.esi.Api;
import eve.tools.esi.model.universe.UniverseName;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ApiTests {

	@Autowired
    private Api api;

	@Test
	public void universeNames() {
		List<Long> ids = new ArrayList<>();
		ids.add((long) 96061222); // character
		ids.add((long) 98522659); // corporation

		List<UniverseName> actual = api.universeNames(ids);

		List<UniverseName> expected = new ArrayList<>();
		expected.add(new UniverseName((long) 96061222, "Tian Khamez", "character"));
		expected.add(new UniverseName((long) 98522659, "Incredible.", "corporation"));

		assertEquals(expected, actual);
	}
}
