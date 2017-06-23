//package localTests;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.Scanner;
//
//import org.junit.Test;
//
//import il.ac.technion.cs.sd.sub.app.SubscriberInitializerImpl;
//
//public class SubscriberInitializerTest {
//
//	private static String readFile(String fileName) {
//		Scanner scanner;
//		try {
//			scanner = new Scanner(new File(fileName));
//		} catch (FileNotFoundException e) {
//			return "";
//
//		}
//		String text = scanner.useDelimiter("\\A").next();
//		scanner.close();
//		return text;
//	}
//
//	@Test
//	public void testCSV() {
//		SubscriberInitializerImpl init = new SubscriberInitializerImpl();
//		init.setupCsv(readFile("../sub-test/src/test/resources/il/ac/technion/cs/sd/sub/test/small.csv"));
//		// assertEquals(init.getJournals().size(), 1);
//		// assertEquals(init.getSubscribers().size(), 2);
//		// assertEquals(init.getCancels().size(), 1);
//
//	}
//
//	@Test
//	public void testJson() {
//		SubscriberInitializerImpl init = new SubscriberInitializerImpl();
//		init.setupJson(readFile("../sub-test/src/test/resources/il/ac/technion/cs/sd/sub/test/small.json"));
//		// assertEquals(init.getJournals().size(), 1);
//		// assertEquals(init.getSubscribers().size(), 1);
//		// assertEquals(init.getCancels().size(), 1);
//	}
//
//}
