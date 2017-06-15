package il.ac.technion.cs.sd.sub.app;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SubscriberInitializerImpl implements SubscriberInitializer {

	Map<String, String> magazines = new HashMap<>();
	Map<String, String> orders = new HashMap<>();
	Map<String, String> cancellations = new HashMap<>();

	@Override
	public CompletableFuture<Void> setupCsv(String csvData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Void> setupJson(String jsonData) {
		// TODO Auto-generated method stub
		return null;
	}

}
