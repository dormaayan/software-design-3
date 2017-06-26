package il.ac.technion.cs.sd.sub.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import databaseImplementations.DataBaseElement;
import databaseInterfaces.IDatabase;

public class SubscriberInitializerImpl implements SubscriberInitializer {

	// Temporary Structures
	Map<String, Map<String, JournalRegistration>> userToJournalsPre = new HashMap<>();
	Map<String, JournalInfo> journalsPre = new HashMap<>();
	Map<String, JournalInfo> journalsPrePre = new HashMap<>();
	Map<String, Map<String, List<Boolean>>> userToJournalHistoryMapPre = new HashMap<>();
	Map<String, Map<String, List<Boolean>>> journalToUserHistoryMapPre = new HashMap<>();

	// The actual real data structures
	IDatabase<String, List<JournalRegistration>> userToJournals;
	IDatabase<String, JournalInfo> journals;
	IDatabase<String, Map<String, List<Boolean>>> userToJournalHistoryMap;
	IDatabase<String, Map<String, List<Boolean>>> journalToUserHistoryMap;

	@Inject
	public SubscriberInitializerImpl(
			@Named("userToJournals") IDatabase<String, List<JournalRegistration>> userToJournals, //
			@Named("journals") IDatabase<String, JournalInfo> journals, //
			@Named("userToJournalHistoryMap") IDatabase<String, Map<String, List<Boolean>>> userToJournalHistoryMap, //
			@Named("journalToUserHistoryMap") IDatabase<String, Map<String, List<Boolean>>> journalToUserHistoryMap) {
		this.userToJournals = userToJournals;
		this.journals = journals;
		this.userToJournalHistoryMap = userToJournalHistoryMap;
		this.journalToUserHistoryMap = journalToUserHistoryMap;
	}

	@Override
	public CompletableFuture<Void> setupCsv(String csvData) {
		String[] lines = csvData.split("[\\r\\n]+");
		for (String line : lines) {
			String[] data = line.split(",");
			if (data[0].equals("subscriber")) {
				String userId = data[1];
				String journalId = data[2];
				subscribeJournal(userId, journalId);
			}
			if (data[0].equals("journal")) {
				String journalId = data[1];
				String price = data[2];
				addJournal(journalId, Integer.parseInt(price));

			}
			if (data[0].equals("cancel")) {
				String userId = data[1];
				String journalId = data[2];
				unsubscribeJournal(userId, journalId);

			}

		}
		initalStructures();
		return null;
	}

	@Override
	public CompletableFuture<Void> setupJson(String jsonData) {
		JSONObject obj;
		try {
			obj = new JSONObject(jsonData.replace("[", "{ \"arr\":[").replaceAll("]", "]}"));
			final JSONArray arr = obj.getJSONArray("arr");
			for (int i = 0; i < arr.length(); i++) {
				String type = ((JSONObject) arr.get(i)).getString("type");
				String userId = "";
				String journalId = "";
				String price = "";
				if (type.equals("cancel")) {
					userId = ((JSONObject) arr.get(i)).getString("user-id");
					journalId = ((JSONObject) arr.get(i)).getString("journal-id");
					unsubscribeJournal(userId, journalId);
				}
				if (type.equals("subscription")) {
					userId = ((JSONObject) arr.get(i)).getString("user-id");
					journalId = ((JSONObject) arr.get(i)).getString("journal-id");
					subscribeJournal(userId, journalId);
				}
				if (type.equals("journal")) {
					journalId = ((JSONObject) arr.get(i)).getString("journal-id");
					price = ((JSONObject) arr.get(i)).getString("price");
					addJournal(journalId, Integer.parseInt(price));
				}
			}
			initalStructures();
			return null;
		} catch (Exception e) {
			throw new RuntimeException();
		}

	}

	private void addJournal(String id, int price) {
		if (journalsPre.containsKey(id)) {
			journalsPre.get(id).declare();
			journalsPre.get(id).setPrice(price);

		} else {
			journalsPre.put(id, new JournalInfo(price, new ArrayList<>(), true));
		}
	}

	private void subscribeJournal(String userId, String journalId) {

		if (journalsPre.containsKey(journalId)) {
			journalsPre.get(journalId).getUsers().add(userId);
		} else {
			journalsPre.put(journalId, new JournalInfo(0, new ArrayList<>()));
			journalsPre.get(journalId).getUsers().add(userId);
		}

		if (userToJournalsPre.get(userId) == null)
			userToJournalsPre.put(userId, new HashMap<>());

		if (userToJournalHistoryMapPre.get(userId) == null)
			userToJournalHistoryMapPre.put(userId, new HashMap<>());

		if (journalToUserHistoryMapPre.get(journalId) == null)
			journalToUserHistoryMapPre.put(journalId, new HashMap<>());

		if (userToJournalHistoryMapPre.get(userId).get(journalId) == null)
			userToJournalHistoryMapPre.get(userId).put(journalId, new ArrayList<>());

		if (journalToUserHistoryMapPre.get(journalId).get(userId) == null)
			journalToUserHistoryMapPre.get(journalId).put(userId, new ArrayList<>());

		userToJournalsPre.get(userId).put(journalId, new JournalRegistration(journalId));
		userToJournalHistoryMapPre.get(userId).get(journalId).add(true);
		journalToUserHistoryMapPre.get(journalId).get(userId).add(true);

	}

	private void unsubscribeJournal(String userId, String journalId) {

		if (journalsPre.containsKey(journalId))
			for (int i = 0; i < journalsPre.get(journalId).getUsers().size(); i++)
				if (journalsPre.get(journalId).getUsers().get(i).equals(userId))
					journalsPre.get(journalId).getUsers().remove(i);

		if (userToJournalsPre.get(userId) == null) {
			userToJournalsPre.put(userId, new HashMap<>());
			userToJournalsPre.get(userId).put(journalId, new JournalRegistration(journalId, false));
		} else if (userToJournalsPre.get(userId).get(journalId) != null) {
			userToJournalsPre.get(userId).get(journalId).cancell();
		}

		if (userToJournalHistoryMapPre.get(userId) == null)
			userToJournalHistoryMapPre.put(userId, new HashMap<>());

		if (journalToUserHistoryMapPre.get(journalId) == null)
			journalToUserHistoryMapPre.put(journalId, new HashMap<>());

		if (userToJournalHistoryMapPre.get(userId).get(journalId) == null)
			userToJournalHistoryMapPre.get(userId).put(journalId, new ArrayList<>());

		if (journalToUserHistoryMapPre.get(journalId).get(userId) == null)
			journalToUserHistoryMapPre.get(journalId).put(userId, new ArrayList<>());

		List<Boolean> history = userToJournalHistoryMapPre.get(userId).get(journalId);
		if (history.size() != 0 && history.get(history.size() - 1) != false) {
			userToJournalHistoryMapPre.get(userId).get(journalId).add(false);
			journalToUserHistoryMapPre.get(journalId).get(userId).add(false);
		}
	}

	private void initalStructures() {
		
		System.out.println("journals:");
		System.out.println(journalsPre);

		

		journals.add(journalsPre.entrySet().stream().distinct().filter(j -> j.getValue().wasDeclared())
				.map(entry -> new DataBaseElement<String, JournalInfo>(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList()));

		userToJournals.add(userToJournalsPre.entrySet().stream()
				.map(entry -> new DataBaseElement<String, List<JournalRegistration>>(entry.getKey(),
						(new ArrayList<JournalRegistration>(entry.getValue().values()).stream().filter(o -> {
							if (!journalsPre.containsKey(o.getJournalID())
									|| !journalsPre.get(o.getJournalID()).wasDeclared())
								return false;
							o.setPrice(journalsPre.get((o.getJournalID())).getPrice());
							return true;
						})

								.collect(Collectors.toList()))))
				.collect(Collectors.toList()));

		userToJournalHistoryMap.add(userToJournalHistoryMapPre.entrySet().stream()
				.map(entry -> new DataBaseElement<String, Map<String, List<Boolean>>>(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList()));

		journalToUserHistoryMap.add(journalToUserHistoryMapPre.entrySet().stream()
				.map(entry -> new DataBaseElement<String, Map<String, List<Boolean>>>(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList()));

	}

}
