package il.ac.technion.cs.sd.sub.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.common.base.Function;

import databaseInterfaces.IStringableFactory;

public class MapFactory<K, V> implements IStringableFactory<Map<K, List<V>>> {
	final Function<String, K> keyParser;
	final Function<K, String> keySerializer;
	final ListFactory<V> valueListFactory;

	public MapFactory(Function<String, K> keyParser, Function<K, String> keySerializer, Function<String, V> valueParser,
			Function<V, String> valueSerializer) {
		this.keyParser = keyParser;
		this.keySerializer = keySerializer;
		valueListFactory = new ListFactory<>(valueParser, valueSerializer);
	}

	@Override
	public CompletableFuture<Map<K, List<V>>> createObject(CompletableFuture<String> e) {
		return e.thenApply(s -> {
			return Arrays.asList(s.split("/")).stream().filter(o -> !o.equals("")).map(str -> str.split(";", 2))//
					.filter(ss -> ss.length!=2).collect(Collectors.toMap(ss -> keyParser.apply(ss[0])//
			, ss -> valueListFactory.createObject(ss[1])));
		});

	}

	@Override
	public CompletableFuture<String> createString(CompletableFuture<Map<K, List<V>>> e) {
		CompletableFuture<String> s = e.thenApply(m -> m.entrySet().stream()//
				.map(entry -> keySerializer.apply(entry.getKey()) + ";"//
						+ valueListFactory.createString(entry.getValue()))//
				.reduce("", (s1, s2) -> s1 + "/" + s2));
		return s;
	}

}