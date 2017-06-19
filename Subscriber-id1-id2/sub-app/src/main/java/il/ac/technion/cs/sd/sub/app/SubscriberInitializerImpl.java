package il.ac.technion.cs.sd.sub.app;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

public class SubscriberInitializerImpl implements SubscriberInitializer {

	Map<String, String> journals = new HashMap<>();
	Map<String, String> subscribers = new HashMap<>();
	Map<String, String> cancels = new HashMap<>();

	@Override
	public CompletableFuture<Void> setupCsv(String csvData) {
		String[] lines = csvData.split("[\\r\\n]+");
		for (String line : lines) {
			String[] data = line.split(",");
			if (data[0].equals("subscriber"))
				subscribers.put(data[1], data[2]);
			if (data[0].equals("journal"))
				journals.put(data[1], data[2]);
			if (data[0].equals("cancel"))
				cancels.put(data[1], data[2]);

		}

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
					cancels.put(userId, journalId);
				}
				if (type.equals("subscription")) {
					userId = ((JSONObject) arr.get(i)).getString("user-id");
					journalId = ((JSONObject) arr.get(i)).getString("journal-id");
					subscribers.put(userId, journalId);
				}
				if (type.equals("journal")) {
					journalId = ((JSONObject) arr.get(i)).getString("journal-id");
					price = ((JSONObject) arr.get(i)).getString("price");
					journals.put(journalId, price);
				}
			}

			return null;
		} catch (Exception e) {
			throw new RuntimeException();
		}

	}

	public Map<String, String> getJournals() {
		return journals;
	}

	public Map<String, String> getSubscribers() {
		return subscribers;
	}

	public Map<String, String> getCancels() {
		return cancels;
	}

}
