package il.ac.technion.cs.sd.sub.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.ac.technion.cs.sd.sub.app.SubscriberInitializer;
import il.ac.technion.cs.sd.sub.app.SubscriberReader;
import il.ac.technion.cs.sd.sub.ext.FutureLineStorage;
import il.ac.technion.cs.sd.sub.ext.LineStorageModule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ExampleTest {

  @Rule public Timeout globalTimeout = Timeout.seconds(30);

  private static Injector setupAndGetInjector(String fileName) throws Exception {
      String fileContents =
        new Scanner(new File(ExampleTest.class.getResource(fileName).getFile())).useDelimiter("\\Z").next();
    Injector injector = Guice.createInjector(new SubscriberModule(), new LineStorageModule());
    SubscriberInitializer si = injector.getInstance(SubscriberInitializer.class);
    CompletableFuture<Void> setup =
        fileName.endsWith("csv") ? si.setupCsv(fileContents) : si.setupJson(fileContents);
    setup.get();
    return injector;
  }

  @Test
  public void testSimpleCsv() throws Exception {
    Injector injector = setupAndGetInjector("small.csv");
    SubscriberReader reader = injector.getInstance(SubscriberReader.class);
    assertEquals(Arrays.asList(true, true, false), reader.getAllSubscriptions("foo1234").get().get("foo1234"));
    assertEquals(0, reader.getMonthlyBudget("foo1234").get().getAsInt());
    assertEquals(100, reader.getMonthlyIncome("foo1234").get().getAsInt());
  }

  @Test
  public void testSimpleJson() throws Exception {
    Injector injector = setupAndGetInjector("small.json");
    SubscriberReader reader = injector.getInstance(SubscriberReader.class);
    assertEquals(100, reader.getMonthlyBudget("foo1234").get().getAsInt());
    assertFalse(reader.getMonthlyBudget("bar1234").get().isPresent());
  }

  //if searchUp == false it will search down.
  CompletableFuture<TreeSet<String>> getAllDataFromLineNumberAndOn(String key, FutureLineStorage lineStorage,
                                                                   final Integer lineNumber, Boolean searchUp) {
    CompletableFuture<TreeSet<String>> setOfData = CompletableFuture.completedFuture(new TreeSet<>());
    return lineStorage.numberOfLines().thenCompose(numberOfLines -> lineStorage.read(lineNumber)
        .thenCompose(readLine -> {
          CompletableFuture<String> readData = null;
          CompletableFuture<String> readKey = null;
  }

  private void aux(String key, FutureLineStorage lineStorage, Integer lineNumber, Boolean searchUp, String dataFromLine, String keyFromLine, int numberOfLines) {
    Boolean shouldBreak = false;
    if (!shouldBreak && searchUp)
      return setOfData.thenCombine(getAllDataFromLineNumberAndOn(key, lineStorage,
          lineNumber + 1, true),
          (data, treeSet) -> {
            data.addAll(treeSet);
            return data;
          });
    else if (!shouldBreak && !searchUp)
      return setOfData.thenCombine(getAllDataFromLineNumberAndOn(key, lineStorage,
          lineNumber - 1, false),
          (data, treeSet) -> {
            data.addAll(treeSet);
            return data;
          });
    else return setOfData;
  }));
  }

  //if searchUp == false it will search down.
  private void getAllDataFromLineNumberAndOn(String key, LineStorage lineStorage,
                                             TreeSet<String> setOfData,
                                             Integer lineNumber, Boolean searchUp)
      throws InterruptedException {
    Integer lineStorageSize = lineStorage.numberOfLines();
    String readLine = lineStorage.read(lineNumber);
    String keyFromLine = getKeyFromLine(readLine);
    while (keyFromLine.compareTo(key) == 0) {
      setOfData.add(getDataFromLine(readLine));
      if (searchUp) lineNumber++;
      else lineNumber--;

      if ((lineNumber < 0) || (lineNumber >= lineStorageSize)) {
        break;
      }
      readLine = lineStorage.read(lineNumber);
      keyFromLine = getKeyFromLine(readLine);
    }
  }
}
