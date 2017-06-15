package il.ac.technion.cs.sd.sub.app;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
		// TODO Auto-generated method stub
		return null;
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
