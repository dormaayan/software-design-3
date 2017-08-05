package il.ac.technion.cs.sd.sub.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.ac.technion.cs.sd.sub.app.SubscriberInitializer;
import il.ac.technion.cs.sd.sub.app.SubscriberReader;
import org.junit.Test;

import java.io.File;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class StaffTest {
  private final TestingLineStorageModule testingLineStorageModule = new TestingLineStorageModule();

  private static Map.Entry<String, List<Boolean>> toEntry(String s, List<Boolean> bs) {
    return new AbstractMap.SimpleEntry(s, bs);
  }

  private static Map<String, List<Boolean>> mapFrom(Map.Entry<String, List<Boolean>>... entries) {
    Map<String, List<Boolean>> $ = new HashMap<>();
    for (Map.Entry<String, List<Boolean>> e : entries)
      $.put(e.getKey(), e.getValue());
    return $;
  }

  private interface MainTest {
    void run(SubscriberReader r) throws Exception;
  }

  private void test(String fileName, int setupTimeoutInMinutes, MainTest t) throws Exception {
    Injector injector = setupAndGetInjector(fileName, setupTimeoutInMinutes);
    mainTest(injector, t);
  }

  private static <T> void futureAssertEquals(CompletableFuture<T> f, T expected) throws Exception {
    try {
      assertEquals(expected, f.get());
    } catch (AssertionError e) {
      StackTraceElement[] stackTrack = Thread.currentThread().getStackTrace();
      int numberOfElementsToRemove = 2;
      StackTraceElement[] newStackTrace = new StackTraceElement[stackTrack.length - numberOfElementsToRemove];
      System.arraycopy(stackTrack, numberOfElementsToRemove, newStackTrace, 0, newStackTrace.length);
      e.setStackTrace(newStackTrace);
      throw e;
    }
  }

  private static <T> T wait(Callable<T> runnable, Duration timeout) throws Exception {
    try {
      return CompletableFuture.supplyAsync(() -> {
        try {
          return runnable.call();
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      }).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    } catch (ExecutionException e) {
      // Since we wrap all exceptions, we need to unstack twice to get the root cause
      Throwable cause = e.getCause().getCause();
      // Fookin' Java
      if (cause instanceof Error)
        throw (Error) cause;
      else if (cause instanceof Exception)
        throw (Exception) cause;
      else
        throw new RuntimeException(e);
    }
  }

  private static void mainTest(Injector i, MainTest test) throws Exception {
    wait(() -> {
      test.run(i.getInstance(SubscriberReader.class));
      return null;
    }, Duration.ofSeconds(60)); // 30 extra seconds as a safeguard, since the futures aren't parallel.
  }

  private Injector setupAndGetInjector(String fileName, int timeoutInMinutes) throws Exception {
    String fileContents =
        new Scanner(new File(StaffTest.class.getResource(fileName).getFile())).useDelimiter("\\Z").next();
    assert !fileContents.isEmpty();
    return wait(() -> {
      // The injector isn't supposed to take any time to create its classes, but just in case...
      Injector injector = Guice.createInjector(new SubscriberModule(), testingLineStorageModule);
      SubscriberInitializer initializer = injector.getInstance(SubscriberInitializer.class);
      if (fileName.endsWith("csv"))
        initializer.setupCsv(fileContents).get();
      else {
        assert fileName.endsWith("json");
        initializer.setupJson(fileContents).get();
      }
      return injector;
    }, Duration.ofMinutes(timeoutInMinutes)); // 3 minutes should be enough setup time even for the large files
  }

  @Test
  public void json1() throws Exception {
    test("medium.json", 1, reader -> {
      futureAssertEquals(reader.isSubscribed("foo", "bar"), Optional.empty());
      futureAssertEquals(reader.isSubscribed("hx", "bar"), Optional.of(false));
      futureAssertEquals(reader.isSubscribed("nn", "tk"), Optional.of(false));
      futureAssertEquals(reader.isSubscribed("hx", "ar"), Optional.of(true));
      futureAssertEquals(reader.isSubscribed("1l", "th"), Optional.of(false));
    });
  }

  @Test
  public void json2() throws Exception {
    test("medium.json", 1, reader -> {
      futureAssertEquals(reader.wasCanceled("foo", "bar"), Optional.empty());
      futureAssertEquals(reader.wasCanceled("nn", "tk"), Optional.of(true));
      futureAssertEquals(reader.wasCanceled("hx", "ar"), Optional.of(true));
      futureAssertEquals(reader.wasCanceled("ju", "bar"), Optional.of(false));
      futureAssertEquals(reader.wasCanceled("wk", "8z"), Optional.of(false));
    });
  }

  @Test
  public void json3() throws Exception {
    test("medium.json", 1, reader -> {
      futureAssertEquals(reader.getSubscribedJournals("foo"), Collections.emptyList());
      futureAssertEquals(reader.getSubscribedJournals("nn"), Arrays.asList("gh", "oa"));
      futureAssertEquals(reader.getSubscribedJournals("ju"), Collections.emptyList());
      futureAssertEquals(reader.getSubscribedJournals("hx"), Collections.singletonList("ar"));
    });
  }

  @Test
  public void json4() throws Exception {
    test("medium.json", 1, reader -> {
      futureAssertEquals(reader.getMonthlyBudget("foo"), OptionalInt.empty());
      futureAssertEquals(reader.getMonthlyBudget("nn"), OptionalInt.of(699716));
      futureAssertEquals(reader.getMonthlyBudget("ju"), OptionalInt.of(0));
      futureAssertEquals(reader.getMonthlyIncome("bar"), OptionalInt.empty());
      futureAssertEquals(reader.getMonthlyIncome("gh"), OptionalInt.of(258138));
    });
  }

  @Test
  public void csv1() throws Exception {
    test("medium.csv", 1, reader -> {
      futureAssertEquals(reader.wasSubscribed("foo", "bar"), Optional.empty());
      futureAssertEquals(reader.wasSubscribed("av", "zz"), Optional.of(true));
      futureAssertEquals(reader.wasSubscribed("av", "bar"), Optional.of(false));
      futureAssertEquals(reader.wasSubscribed("lm", "n9"), Optional.of(false));
      futureAssertEquals(reader.wasSubscribed("xf", "uu"), Optional.of(true));
    });
  }

  @Test
  public void csv2() throws Exception {
    test("medium.csv", 1, reader -> {
      futureAssertEquals(reader.isCanceled("foo", "bar"), Optional.empty());
      futureAssertEquals(reader.isCanceled("av", "zz"), Optional.of(true));
      futureAssertEquals(reader.isCanceled("av", "bar"), Optional.of(false));
      futureAssertEquals(reader.isCanceled("lm", "n9"), Optional.of(false));
      futureAssertEquals(reader.isCanceled("1q", "n4"), Optional.of(false));
    });
  }

  @Test
  public void csv3() throws Exception {
    test("medium.csv", 1, reader -> {
      futureAssertEquals(reader.getAllSubscriptions("foo"), Collections.EMPTY_MAP);
      futureAssertEquals(reader.getAllSubscriptions("av"), mapFrom(
          toEntry("zz", Arrays.asList(true, false)),
          toEntry("fl", Arrays.asList(true, false, true, true)),
          toEntry("fq", Collections.singletonList(false))
      ));
      futureAssertEquals(reader.getSubscribers("foo"), Collections.EMPTY_MAP);
      futureAssertEquals(reader.getSubscribers("fl"), mapFrom(
          toEntry("av", Arrays.asList(true, false, true, true)),
          toEntry("wj", Collections.singletonList(false)),
          toEntry("zn", Collections.singletonList(false)),
          toEntry("ma", Collections.singletonList(true))
      ));
    });
  }

  @Test
  public void csv4() throws Exception {
    test("medium.csv", 1, reader -> {
      futureAssertEquals(reader.getSubscribedUsers("foo"), Collections.EMPTY_LIST);
      futureAssertEquals(reader.getSubscribedUsers("fl"), Arrays.asList("av", "ma"));
    });
  }

  @Test
  public void largeJson() throws Exception {
    test("large.json", 3, reader -> {
      futureAssertEquals(reader.wasSubscribed("nek", "548"), Optional.of(false));
      futureAssertEquals(reader.isCanceled("exj", "oxy"), Optional.of(true));
      futureAssertEquals(reader.getAllSubscriptions("exj"), mapFrom(
          toEntry("oxy", Arrays.asList(true, true, true, false)),
          toEntry("bwu", Collections.singletonList(true)),
          toEntry("bzh", Collections.singletonList(true)),
          toEntry("uxo", Collections.singletonList(false))
      ));
      futureAssertEquals(reader.getSubscribers("oxy"), mapFrom(
          toEntry("exj", Arrays.asList(true, true, true, false)),
          toEntry("8kt", Collections.singletonList(false)),
          toEntry("2gy", Collections.singletonList(true)),
          toEntry("4ew", Collections.singletonList(false)),
          toEntry("ixe", Collections.singletonList(true)),
          toEntry("cq7", Collections.singletonList(false)),
          toEntry("o4p", Collections.singletonList(false)),
          toEntry("boh", Collections.singletonList(true)),
          toEntry("phb", Collections.singletonList(false))
      ));
      futureAssertEquals(reader.getSubscribedUsers("oxy"), Arrays.asList("2gy", "boh", "ixe"));
    });
  }

  @Test
  public void largeCsv() throws Exception {
    test("large.csv", 3, reader -> {
      futureAssertEquals(reader.isSubscribed("wic", "ch3"), Optional.of(true));
      futureAssertEquals(reader.wasCanceled("wic", "ch3"), Optional.of(true));
      futureAssertEquals(reader.getSubscribedJournals("wic"), Arrays.asList("ce5", "ch3", "etd", "ucp"));
      futureAssertEquals(reader.getMonthlyBudget("wic"), OptionalInt.of(1458414));
      futureAssertEquals(reader.getMonthlyIncome("ch3"), OptionalInt.of(398502));
    });
  }
}
