package library;

import java.util.concurrent.CompletableFuture;

import com.google.inject.Inject;

import il.ac.technion.cs.sd.sub.ext.FutureLineStorage;

public class SuccecefulLineStorage {
	private final FutureLineStorage store;

	@Inject
	public SuccecefulLineStorage(FutureLineStorage store) {
		this.store = store;
	}

	public CompletableFuture<Void> appendLine(String line) {
		return store.appendLine(line).thenCompose(b -> b ? null : appendLine(line));
	}

	public CompletableFuture<String> read(int i) {
		return store.read(i).thenCompose(o -> !o.isPresent() ? read(i)//
				: CompletableFuture.completedFuture(o.get()));
	}

	public CompletableFuture<Integer> numberOfLines() {
		return store.numberOfLines().thenCompose(o -> !o.isPresent() ? numberOfLines()//
				: CompletableFuture.completedFuture(o.getAsInt()));
	}

}
