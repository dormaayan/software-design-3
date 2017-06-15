package localTests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

public class SubscriberInitializerTest {

	private static String readFile(String fileName) {
		Scanner scanner;
		try {
			scanner = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			return "";

		}
		String text = scanner.useDelimiter("\\A").next();
		scanner.close();
		return text;
	}

	@Test
	public void testCSV() {
		System.out.println(readFile("../sub-test/src/test/resources/il/ac/technion/cs/sd/sub/test/small.csv"));
	}

	@Test
	public void testJson() {
		System.out.println(readFile("../sub-test/src/test/resources/il/ac/technion/cs/sd/sub/test/small.json"));
	}

}
