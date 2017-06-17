package il.ac.technion.cs.sd.sub.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import databaseInterfaces.IDatabase;
import databaseInterfaces.IDatabaseElement;

public class DataBaseTestImpl<Key extends Comparable<Key>, Value> implements IDatabase<Key, Value> {

	Map<Key, Value> m = new HashMap<>();

	@Override
	public CompletableFuture<Integer> getNumberOfElements() {
		return CompletableFuture.completedFuture(m.size());
	}

	@Override
	public void add(List<? extends IDatabaseElement<Key, Value>> elements) {
		elements.stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
				.forEach(e -> m.put(e.getKey(), e.getValue()));

	}

	@Override
	public CompletableFuture<Optional<Value>> findElementByID(Key key) {
		return CompletableFuture.completedFuture(Optional.of(m.get(key)));
	}

}
