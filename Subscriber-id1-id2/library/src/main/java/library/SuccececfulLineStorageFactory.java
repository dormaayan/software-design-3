package library;

import java.util.concurrent.CompletableFuture;

import com.google.inject.Inject;

import il.ac.technion.cs.sd.sub.ext.FutureLineStorageFactory;

public class SuccececfulLineStorageFactory {
	final FutureLineStorageFactory factory;

	@Inject
	public SuccececfulLineStorageFactory(FutureLineStorageFactory factory) {
		this.factory = factory;
	}

	public CompletableFuture<SuccecefulLineStorage> open(String name) {
		return factory.open(name).thenCompose(o -> !o.isPresent() ? open(name)//
				: CompletableFuture.completedFuture(new SuccecefulLineStorage(o.get())));
	}

}
