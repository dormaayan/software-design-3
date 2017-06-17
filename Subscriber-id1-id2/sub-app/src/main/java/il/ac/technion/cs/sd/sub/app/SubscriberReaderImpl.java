package il.ac.technion.cs.sd.sub.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import databaseInterfaces.IDatabase;

public class SubscriberReaderImpl implements SubscriberReader {

	IDatabase<String, String> userToCurrentJournals;
	IDatabase<String, String> userToAllJournals;
	IDatabase<String, String> journalToPrice;
	IDatabase<String, String> journalToUsers;

	private List<String> extractList(String s) {
		return Arrays.asList((s.substring(1).substring(0, s.substring(1).length() - 1).split(",")));
	}

	@Override
	public CompletableFuture<Optional<Boolean>> isSubscribed(String userId, String journalId) {
		return userToCurrentJournals.findElementByID(userId).thenApply(jid -> {
			if (!jid.isPresent())
				return Optional.empty();
			return Optional.of(extractList(jid.get()).contains(journalId));

		});
	}

	@Override
	public CompletableFuture<Optional<Boolean>> wasSubscribed(String userId, String journalId) {
		return userToAllJournals.findElementByID(userId).thenApply(jid -> {
			if (!jid.isPresent())
				return Optional.empty();
			return Optional.of(extractList(jid.get()).contains(journalId));

		});
	}

	@Override
	public CompletableFuture<Optional<Boolean>> isCanceled(String userId, String journalId) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Optional<Boolean>> wasCanceled(String userId, String journalId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<String>> getSubscribedJournals(String userId) {
		return userToCurrentJournals.findElementByID(userId).thenApply(lst -> {
			if (!lst.isPresent())
				return new ArrayList<String>();
			return extractList(lst.get());
		});

	}

	@Override
	public CompletableFuture<Map<String, List<Boolean>>> getAllSubscriptions(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<OptionalInt> getMonthlyBudget(String userId) {
		return userToCurrentJournals.findElementByID(userId).thenCompose(lst -> {
			if (!lst.isPresent())
				return CompletableFuture.completedFuture(OptionalInt.empty());
			List<CompletableFuture<String>> tmp = extractList(lst.get()).stream()
					.map(j -> journalToPrice.findElementByID(j).thenApply(x -> x.get())).collect(Collectors.toList());
			CompletableFuture<List<String>> prices = sequence(tmp);
			return prices.thenApply(
					priceList -> OptionalInt.of(priceList.stream().mapToInt(p -> Integer.parseInt(p)).sum()));

		});

	}

	static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> com) {
		return CompletableFuture.allOf(com.toArray(new CompletableFuture[com.size()]))
				.thenApply(v -> com.stream().map(CompletableFuture::join).collect(Collectors.toList()));
	}

	@Override
	public CompletableFuture<List<String>> getSubscribedUsers(String journalId) {
		return journalToUsers.findElementByID(journalId).thenApply(lst -> {
			if (!lst.isPresent())
				return new ArrayList<String>();
			return extractList(lst.get());
		});
	}

	// the price of the journal * numberOfSubscriber
	@Override
	public CompletableFuture<OptionalInt> getMonthlyIncome(String journalId) {
		return journalToUsers.findElementByID(journalId).thenCompose(lst -> {
			if (!lst.isPresent())
				return CompletableFuture.completedFuture(OptionalInt.empty());
			int n = extractList(lst.get()).size();
			return journalToPrice.findElementByID(journalId)
					.thenApply(p -> OptionalInt.of(Integer.parseInt(p.get()) * n));
		});
	}

	@Override
	public CompletableFuture<Map<String, List<Boolean>>> getSubscribers(String journalId) {
		// TODO Auto-generated method stub
		return null;
	}

}
