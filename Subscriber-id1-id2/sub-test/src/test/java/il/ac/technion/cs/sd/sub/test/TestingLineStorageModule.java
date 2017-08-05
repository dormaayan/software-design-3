package il.ac.technion.cs.sd.sub.test;

import com.google.inject.AbstractModule;
import il.ac.technion.cs.sd.sub.ext.FutureLineStorage;
import il.ac.technion.cs.sd.sub.ext.FutureLineStorageFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

class TestingLineStorageModule extends AbstractModule {
  private final FutureLineStorageFactoryImpl t = new FutureLineStorageFactoryImpl();

  @Override
  public void configure() {
    bind(FutureLineStorageFactory.class).toInstance(t);
  }

  private interface ThrowingSupplier<T> {
    T get() throws Exception;
  }

  private static final Random r = new Random();
  private static <T> CompletableFuture<T> errorOrT(T onError, ThrowingSupplier<T> ts) {
    return r.nextBoolean() ? CompletableFuture.completedFuture(onError) : swallowException(ts);
  }

  private static <T> CompletableFuture<T> swallowException(ThrowingSupplier<T> ts) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return ts.get();
      } catch (Exception e) {
        throw new AssertionError(e);
      }
    });
  }

  private static class FutureLineStorageFactoryImpl implements FutureLineStorageFactory {
    private final Random r = new Random();
    private final Map<String, FutureLineStorage> map = new HashMap<>();

    @Override
    public CompletableFuture<Optional<FutureLineStorage>> open(String fileName) {
	  return errorOrT(Optional.empty(),() -> {
		synchronized(FutureLineStorageFactoryImpl.this) {
          if (!map.containsKey(fileName))
            map.put(fileName, new FutureLineStorageImpl());
          Thread.sleep(map.size() * 100);
          return Optional.of(map.get(fileName));
		}
      }); 
    }
  }

  private static class FutureLineStorageImpl implements FutureLineStorage {
    private final List<String> lines = new ArrayList<>();

    @Override
    public CompletableFuture<Boolean> appendLine(String s) {
      return errorOrT(false, () -> {
        lines.add(s);
        return true;
      });
    }

    @Override
    public CompletableFuture<Optional<String>> read(int i) {
      return errorOrT(Optional.empty(), () -> {
        String $ = lines.get(i);
        Thread.sleep($.length());
        return Optional.of($);
      });
    }

    @Override
    public CompletableFuture<OptionalInt> numberOfLines() {
      return errorOrT(OptionalInt.empty(), () -> {
        Thread.sleep(100);
        return OptionalInt.of(lines.size());
      });
    }
  }
}
