package tamaized.voidfog;

import net.neoforged.fml.ModList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModLoadsTests {

	@Test
	public void isLoaded() {
		assertTrue(ModList.get().isLoaded(VoidFog.MODID));
	}

}
