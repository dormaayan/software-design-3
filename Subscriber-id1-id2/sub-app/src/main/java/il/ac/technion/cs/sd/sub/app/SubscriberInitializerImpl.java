package il.ac.technion.cs.sd.sub.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import databaseInterfaces.IDatabase;

public class SubscriberInitializerImpl implements SubscriberInitializer {

	// Temporary Structures
	Map<String, Map<String, JournalRegistration>> userToJournalsPre = new HashMap<>();
	Map<String, JournalInfo> journalsPre = new HashMap<>();
	Map<String, Map<String, List<Boolean>>> userToJournalHistoryMapPre = new HashMap<>();
	Map<String, Map<String, List<Boolean>>> journalToUserHistoryMapPre = new HashMap<>();

	// The actual real data structures
	IDatabase<String, List<JournalRegistration>> userToJournals;
	IDatabase<String, JournalInfo> journals;
	IDatabase<String, Map<String, List<Boolean>>> userToJournalHistoryMap;
	IDatabase<String, Map<String, List<Boolean>>> journalToUserHistoryMap;

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
		journalsPre.put(id, new JournalInfo(price, new ArrayList<>()));
	}

	private void subscribeJournal(String userId, String journalId) {
		if (userToJournalsPre.get(userId) == null)
			userToJournalsPre.put(userId, new HashMap<>());

		if (userToJournalHistoryMapPre.get(userId) == null)
			userToJournalHistoryMapPre.put(userId, new HashMap<>());

		if (journalToUserHistoryMapPre.get(userId) == null)
			journalToUserHistoryMapPre.put(userId, new HashMap<>());

		if (userToJournalHistoryMapPre.get(userId).get(journalId) == null)
			userToJournalHistoryMapPre.get(userId).put(journalId, new ArrayList<>());

		if (journalToUserHistoryMapPre.get(userId).get(journalId) == null)
			journalToUserHistoryMapPre.get(userId).put(journalId, new ArrayList<>());

		userToJournalsPre.get(userId).put(journalId, new JournalRegistration(journalId));
		userToJournalHistoryMapPre.get(userId).get(journalId).add(true);
		journalToUserHistoryMapPre.get(journalId).get(userId).add(true);

	}

	private void unsubscribeJournal(String userId, String journalId) {

		if (userToJournalsPre.get(userId) == null) {
			userToJournalsPre.put(userId, new HashMap<>());
			userToJournalsPre.get(userId).put(journalId, new JournalRegistration(journalId, false));
		} else {
			userToJournalsPre.get(userId).get(journalId).cancell();
		}

		if (userToJournalHistoryMapPre.get(userId) == null)
			userToJournalHistoryMapPre.put(userId, new HashMap<>());

		if (journalToUserHistoryMapPre.get(userId) == null)
			journalToUserHistoryMapPre.put(userId, new HashMap<>());

		if (userToJournalHistoryMapPre.get(userId).get(journalId) == null)
			userToJournalHistoryMapPre.get(userId).put(journalId, new ArrayList<>());

		if (journalToUserHistoryMapPre.get(userId).get(journalId) == null)
			journalToUserHistoryMapPre.get(userId).put(journalId, new ArrayList<>());

		List<Boolean> history = userToJournalHistoryMapPre.get(userId).get(journalId);
		if (history.size() != 0 && history.get(history.size() - 1) != false) {
			userToJournalHistoryMapPre.get(userId).get(journalId).add(false);
			journalToUserHistoryMapPre.get(journalId).get(userId).add(false);

		}

	}

	private void initalStructures() {

	}

}
