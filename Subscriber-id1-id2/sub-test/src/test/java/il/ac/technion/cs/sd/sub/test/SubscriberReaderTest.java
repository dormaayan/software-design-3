package il.ac.technion.cs.sd.sub.test;

import static org.junit.Assert.*;

import java.io.File;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import il.ac.technion.cs.sd.sub.app.SubscriberInitializer;
import il.ac.technion.cs.sd.sub.app.SubscriberInitializerImpl;
import il.ac.technion.cs.sd.sub.app.SubscriberReader;

public class SubscriberReaderTest {

	@SuppressWarnings("resource")
	private static Injector setupAndGetInjector(String fileName) throws Exception {
		String fileContents = new Scanner(new File(ExampleTest.class.getResource(fileName).getFile()))
				.useDelimiter("\\Z").next();
		Injector injector = Guice.createInjector(new SubscriberModule(), new FakeLineStorageMoudle());
		SubscriberInitializer si = injector.getInstance(SubscriberInitializer.class);
		CompletableFuture<Void> setup = fileName.endsWith("csv") ? si.setupCsv(fileContents)
				: si.setupJson(fileContents);
		setup.get();
		return injector;
	}

	@Test
	public void testCSV0() throws Exception {
		Injector injector = setupAndGetInjector("small.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertEquals(Arrays.asList(true, true, false), reader.getAllSubscriptions("foo1234").get().get("foo1234"));
	}

	@Test
	public void testCSV1() throws Exception {
		Injector injector = setupAndGetInjector("small.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertTrue(reader.getSubscribedJournals("sdvdsv").get().isEmpty());
	}

	@Test
	public void testCSV2() throws Exception {
		Injector injector = setupAndGetInjector("small.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertEquals(reader.getSubscribedJournals("foo123").get(), Arrays.asList());
	}

	@Test
	public void testCSV3() throws Exception {
		Injector injector = setupAndGetInjector("small.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertEquals(reader.getMonthlyBudget("bar1234").get().getAsInt(), 100);
	}

	@Test
	public void testCSV4() throws Exception {
		Injector injector = setupAndGetInjector("small.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertEquals(reader.getSubscribedJournals("bar1234").get(), Arrays.asList("foo1234"));
	}

	@Test
	public void testCSV5() throws Exception {
		Injector injector = setupAndGetInjector("small.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertEquals(reader.getAllSubscriptions("bar1234").get().get("foo1234"), Arrays.asList(true));
	}

	@Test
	public void testJson0() throws Exception {
		Injector injector = setupAndGetInjector("small.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertEquals(Arrays.asList(false, true), reader.getAllSubscriptions("foo1234").get().get("foo1234"));
	}

	@Test
	public void testJson1() throws Exception {
		Injector injector = setupAndGetInjector("small.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertTrue(reader.getSubscribedJournals("sdvdsv").get().isEmpty());
	}

	@Test
	public void testJson2() throws Exception {
		Injector injector = setupAndGetInjector("small.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertEquals(reader.getSubscribedJournals("foo123").get(), Arrays.asList());
	}
	

	@Test
	public void testJson3() throws Exception {
		Injector injector = setupAndGetInjector("small.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertEquals(reader.getMonthlyBudget("bar1234").get().getAsInt(), 100);
	}

	@Test
	public void testJson4() throws Exception {
		Injector injector = setupAndGetInjector("small.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertEquals(reader.getSubscribedJournals("bar1234").get(), Arrays.asList("foo1234"));
	}

	@Test
	public void testJson5() throws Exception {
		Injector injector = setupAndGetInjector("small.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertEquals(reader.getAllSubscriptions("bar1234").get().get("foo1234"), Arrays.asList(true));
	}


}
