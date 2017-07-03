package il.ac.technion.cs.sd.sub.test;

import static org.junit.Assert.*;

import java.io.File;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import il.ac.technion.cs.sd.sub.app.SubscriberInitializerImpl;
import library.FakeLineStorageMoudle;

public class SubscriberInitializerTest {

	@SuppressWarnings("resource")
	private static SubscriberInitializerImpl setupAndGetInjector(String fileName) throws Exception {
		String fileContents = new Scanner(new File(ExampleTest.class.getResource(fileName).getFile()))
				.useDelimiter("\\Z").next();
		Injector injector = Guice.createInjector(new SubscriberModule(), new FakeLineStorageMoudle());
		SubscriberInitializerImpl si = injector.getInstance(SubscriberInitializerImpl.class);
		CompletableFuture<Void> setup = fileName.endsWith("csv") ? si.setupCsv(fileContents)
				: si.setupJson(fileContents);
		setup.get();
		return si;
	}

	@Test
	public void testCSV0() throws Exception {
		SubscriberInitializerImpl init = setupAndGetInjector("small.csv");
		assertEquals(init.getJournalsPre().size(), 1);

	}

	@Test
	public void testCSV1() throws Exception {
		SubscriberInitializerImpl init = setupAndGetInjector("small.csv");
		assertEquals(init.getUserToJournalsPre().size(), 2);

	}

	@Test
	public void testCSV2() throws Exception {
		SubscriberInitializerImpl init = setupAndGetInjector("small.csv");
		assertEquals(init.getJournalToUserHistoryMapPre().get("foo1234").size(), 2);
	}

	@Test
	public void testCSV3() throws Exception {
		SubscriberInitializerImpl init = setupAndGetInjector("small.csv");
		assertEquals(init.getJournalToUserHistoryMapPre().get("foo1234").get("foo1234"),
				Arrays.asList(true, true, false));
	}

	@Test
	public void testJson0() throws Exception {
		SubscriberInitializerImpl init = setupAndGetInjector("small.json");
		assertEquals(init.getJournalsPre().size(), 1);

	}

	@Test
	public void testJson1() throws Exception {
		SubscriberInitializerImpl init = setupAndGetInjector("small.json");
		assertEquals(init.getUserToJournalsPre().size(), 1);

	}

	@Test
	public void testJson2() throws Exception {
		SubscriberInitializerImpl init = setupAndGetInjector("small.json");
		assertEquals(init.getJournalToUserHistoryMapPre().get("foo1234").size(), 1);
	}

	@Test
	public void testJson3() throws Exception {
		SubscriberInitializerImpl init = setupAndGetInjector("small.json");
		assertEquals(init.getJournalToUserHistoryMapPre().get("foo1234").get("foo1234"), Arrays.asList(false, true));
	}

}
