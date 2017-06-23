package il.ac.technion.cs.sd.sub.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import databaseInterfaces.IDatabase;

public class SubscriberReaderImpl implements SubscriberReader {

	//The databases
	IDatabase<String, List<JournalRegistration>> userToJournals;
	IDatabase<String, JournalInfo> journals;
	IDatabase<String, Map<String, List<Boolean>>> userToJournalHistoryMap;
	IDatabase<String, Map<String, List<Boolean>>> journalToUserHistoryMap;

	private CompletableFuture<Optional<JournalRegistration>> findRegistrationToJournal(String userId,
			String journalId) {
		return userToJournals.findElementByID(userId)//
				.thenApply(o -> o.flatMap(lst -> lst.stream()//
						.filter(jr -> journalId.equals(jr.getJournalID())).findAny()));
	}

	@Override
	public CompletableFuture<Optional<Boolean>> isSubscribed(String userId, String journalId) {
		return findRegistrationToJournal(userId, journalId)//
				.thenApply(o -> o.map(jr -> jr.isSubscribed()));
	}

	@Override
	public CompletableFuture<Optional<Boolean>> wasSubscribed(String userId, String journalId) {
		return findRegistrationToJournal(userId, journalId)//
				.thenApply(o -> o.map(jr -> jr.wasSubscribed()));
	}

	@Override
	public CompletableFuture<Optional<Boolean>> isCanceled(String userId, String journalId) {
		return findRegistrationToJournal(userId, journalId)//
				.thenApply(o -> o.map(jr -> !jr.isSubscribed()));
	}

	@Override
	public CompletableFuture<Optional<Boolean>> wasCanceled(String userId, String journalId) {
		return findRegistrationToJournal(userId, journalId)//
				.thenApply(o -> o.map(jr -> jr.wasCanceled()));
	}

	private CompletableFuture<List<JournalRegistration>> getSubscritions(String userId) {
		return userToJournals.findElementByID(userId)//
				.thenApply(o -> o.orElse(new ArrayList<>()).stream()//
						.filter(jr -> jr.isSubscribed()).collect(Collectors.toList()));
	}

	@Override
	public CompletableFuture<List<String>> getSubscribedJournals(String userId) {
		return getSubscritions(userId).thenApply(lst -> lst.stream()//
				.map(jr -> jr.getJournalID()).collect(Collectors.toList()));
	}

	@Override
	public CompletableFuture<Map<String, List<Boolean>>> getAllSubscriptions(String userId) {
		return userToJournalHistoryMap.findElementByID(userId)//
				.thenApply(map -> map.orElse(new HashMap<>()));
	}

	@Override
	public CompletableFuture<OptionalInt> getMonthlyBudget(String userId) {
		return getSubscritions(userId).thenApply(lst -> lst.stream()//
				.mapToInt(jr -> jr.getPrice()).reduce(Integer::sum));
	}

	@Override
	public CompletableFuture<List<String>> getSubscribedUsers(String journalId) {
		return journals.findElementByID(journalId)//
				.thenApply(o -> o.map(jd -> jd.getUsers()).orElse(new ArrayList<String>()));
	}

	// the price of the journal * numberOfSubscriber
	@Override
	public CompletableFuture<OptionalInt> getMonthlyIncome(String journalId) {
		return journals.findElementByID(journalId).thenApply(o -> o.map(jd -> jd.getPrice() * jd.getUsers().size())//
				.map(OptionalInt::of).orElse(OptionalInt.empty()));
	}

	@Override
	public CompletableFuture<Map<String, List<Boolean>>> getSubscribers(String journalId) {
		return journalToUserHistoryMap.findElementByID(journalId)//
				.thenApply(map -> map.orElse(new HashMap<>()));
	}

}
