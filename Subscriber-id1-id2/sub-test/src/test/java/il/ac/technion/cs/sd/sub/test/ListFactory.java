package il.ac.technion.cs.sd.sub.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.common.base.Function;

import databaseInterfaces.IStringableFactory;

public class ListFactory<T> implements IStringableFactory<List<T>> {
	final Function<String, T> parser;
	final Function<T, String> serializer;

	public ListFactory(Function<String, T> parser, Function<T, String> serializer) {
		this.parser = parser;
		this.serializer = serializer;
	}

	public List<T> createObject(String str) {
		if (str.equals("") || str == null)
			return new ArrayList<>();
		return Arrays.asList(str.split(";"))//
				.stream().map(string -> parser.apply(string))
				.collect(Collectors.toList());
	}

	@Override
	public CompletableFuture<List<T>> createObject(CompletableFuture<String> s) {
		return s.thenApply(str -> createObject(str));
	}

	public String createString(List<T> lst) {
		return lst.stream().map(t -> serializer.apply(t))//
				.reduce((s1, s2) -> s1 + ";" + s2).orElse("");
	}

	@Override
	public CompletableFuture<String> createString(CompletableFuture<List<T>> e) {
		return e.thenApply(lst -> createString(lst));
	}

}