package library;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import il.ac.technion.cs.sd.sub.ext.FutureLineStorage;

/**
 * A storer to be used for storing where a functional storer is needed. Uses a
 * list of strings to simulate the file. Should only be used for testing as it
 * is not persistent. Also emulates the timing of LineStorage if initialized to
 * do so
 */
public class TestStorer implements FutureLineStorage {
	private final List<String> lst = new ArrayList<>();
	private final Random rand = new Random();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lst == null) ? 0 : lst.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestStorer other = (TestStorer) obj;
		if (lst == null) {
			if (other.lst != null)
				return false;
		} else if (!lst.equals(other.lst))
			return false;
		return true;
	}

	@Override
	public CompletableFuture<Boolean> appendLine(String line) {
		boolean b = rand.nextBoolean();
		if (b) {
			lst.add(line);
		}
		return CompletableFuture.completedFuture(b);
	}

	@Override
	public CompletableFuture<Optional<String>> read(int lineNumber) {
		if (!rand.nextBoolean())
			return CompletableFuture.completedFuture(Optional.empty());
		String $ = lst.get(lineNumber);
		return CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep($.length());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).thenApply(v -> Optional.of($));
	}

	@Override
	public CompletableFuture<OptionalInt> numberOfLines() {
		if(!rand.nextBoolean())
			return CompletableFuture.completedFuture(OptionalInt.empty());
		return CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).thenApply(v -> OptionalInt.of(lst.size()));
	}
}
