package il.ac.technion.cs.sd.sub.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import il.ac.technion.cs.sd.sub.ext.FutureLineStorage;
import il.ac.technion.cs.sd.sub.ext.FutureLineStorageFactory;

public class TestStorerFactory implements FutureLineStorageFactory {

	private final Map<String, TestStorer> store = new HashMap<>();
	private final Random rand = new Random();

	@Override
	public CompletableFuture<Optional<FutureLineStorage>> open(String name) {
		if(!rand.nextBoolean())
			return CompletableFuture.completedFuture(Optional.empty());
		if (!store.containsKey(name))
			store.put(name, new TestStorer());
		return CompletableFuture.runAsync(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(store.size()*100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).thenApply(v -> Optional.of((FutureLineStorage)store.get(name)));
	}

}
