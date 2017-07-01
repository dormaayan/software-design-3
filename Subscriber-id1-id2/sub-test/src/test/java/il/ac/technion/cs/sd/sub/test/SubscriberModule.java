package il.ac.technion.cs.sd.sub.test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import databaseImplementations.Database;
import databaseInterfaces.IDatabase;
import databaseInterfaces.IStringableFactory;
import il.ac.technion.cs.sd.sub.app.JournalInfo;
import il.ac.technion.cs.sd.sub.app.JournalRegistration;
import il.ac.technion.cs.sd.sub.app.SubscriberInitializer;
import il.ac.technion.cs.sd.sub.app.SubscriberInitializerImpl;
import il.ac.technion.cs.sd.sub.app.SubscriberReader;
import il.ac.technion.cs.sd.sub.app.SubscriberReaderImpl;
import library.SuccececfulLineStorageFactory;
import library.SuccecefulLineStorage;

// This module is in the testing project, so that it could easily bind all dependencies from all levels.
public class SubscriberModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(SubscriberInitializer.class).to(SubscriberInitializerImpl.class);
		bind(SubscriberReader.class).to(SubscriberReaderImpl.class);
	}

	@Provides
	@Named("userToJournals")
	protected IDatabase<String, List<JournalRegistration>> userToJournalsProvider(SuccececfulLineStorageFactory f)
			throws InterruptedException, ExecutionException {
		CompletableFuture<SuccecefulLineStorage> open = f.open("userToJournals keys");
		CompletableFuture<SuccecefulLineStorage> open2 = f.open("userToJournals values");
		return new Database<>(//
				open.get()//
				, open2.get()//
				, new StringFactory()//
				, new ListFactory<>(JournalRegistration::parse, JournalRegistration::serialize));
	}

	@Provides
	@Named("journals")
	protected IDatabase<String, JournalInfo> journalsProvider(SuccececfulLineStorageFactory f)
			throws InterruptedException, ExecutionException {
		CompletableFuture<SuccecefulLineStorage> open = f.open("journals keys");
		CompletableFuture<SuccecefulLineStorage> open2 = f.open("journals values");
		return new Database<>(open.get()//
				, open2.get()//
				, new StringFactory()//
				, new IStringableFactory<JournalInfo>() {
					@Override
					public CompletableFuture<JournalInfo> createObject(CompletableFuture<String> s) {
						return s.thenApply(JournalInfo::parse);
					}

					@Override
					public CompletableFuture<String> createString(CompletableFuture<JournalInfo> e) {
						return e.thenApply(JournalInfo::serialize);
					}
				}//
		);
	}

	@Provides
	@Named("userToJournalHistoryMap")
	protected IDatabase<String, Map<String, List<Boolean>>> userToJournalHistoryMapProvider(
			SuccececfulLineStorageFactory f) throws InterruptedException, ExecutionException {
		CompletableFuture<SuccecefulLineStorage> open = f.open("userToJournalHistoryMap keys");
		CompletableFuture<SuccecefulLineStorage> open2 = f.open("userToJournalHistoryMap values");
		return new Database<String, Map<String, List<Boolean>>>(//
				open.get()//
				, open2.get()//
				, new StringFactory()//
				, new MapFactory<>(s -> s, s -> s, s -> s.equals("t"), b -> b ? "t" : "f"));
	}

	@Provides
	@Named("journalToUserHistoryMap")
	protected IDatabase<String, Map<String, List<Boolean>>> journalToUserHistoryMapProvider(
			SuccececfulLineStorageFactory f) throws InterruptedException, ExecutionException {
		CompletableFuture<SuccecefulLineStorage> open = f.open("journalToUserHistoryMap keys");
		CompletableFuture<SuccecefulLineStorage> open2 = f.open("journalToUserHistoryMap values");
		return new Database<String, Map<String, List<Boolean>>>(//
				open.get()//
				, open2.get()//
				, new StringFactory()//
				, new MapFactory<>(s -> s, s -> s, s -> s.equals("t"), b -> b ? "t" : "f"));
	}

	public class StringFactory implements IStringableFactory<String> {
		@Override
		public CompletableFuture<String> createObject(CompletableFuture<String> s) {
			return s;
		}

		@Override
		public CompletableFuture<String> createString(CompletableFuture<String> e) {
			return e;
		}
	}

}
