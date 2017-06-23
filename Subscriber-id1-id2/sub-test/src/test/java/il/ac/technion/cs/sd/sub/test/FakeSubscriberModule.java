package il.ac.technion.cs.sd.sub.test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
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
import il.ac.technion.cs.sd.sub.ext.FutureLineStorageFactory;
import il.ac.technion.cs.sd.sub.test.SubscriberModule.StringFactory;
import library.SuccececfulLineStorageFactory;

public class FakeSubscriberModule extends AbstractModule {
	
	private static IDatabase<String, List<JournalRegistration>> userToJournals;
	private static IDatabase<String, JournalInfo> journals;
	private static IDatabase<String, Map<String, List<Boolean>>> userToJournalHistoryMap;
	private static IDatabase<String, Map<String, List<Boolean>>> journalToUserHistoryMap;

	
	@Override
	protected void configure() {
		bind(SubscriberInitializer.class).to(SubscriberInitializerImpl.class);
		bind(SubscriberReader.class).to(SubscriberReaderImpl.class);
		bind(FutureLineStorageFactory.class).toProvider(FakeFactoryProvider.class);
	}
	
	@Provides
	@Named("userToJournals")
	protected IDatabase<String, List<JournalRegistration>> userToJournalsProvider(SuccececfulLineStorageFactory f)
			throws InterruptedException, ExecutionException {
		return userToJournals != null ? userToJournals//
				: (userToJournals = new Database<>(//
						f.open("userToJournals keys").get()//
						, f.open("userToJournals values").get()//
						, new StringFactory()//
						, new ListFactory<>(JournalRegistration::parse, JournalRegistration::serialize)));
	}

	@Provides
	@Named("journals")
	protected IDatabase<String, JournalInfo> journalsProvider(SuccececfulLineStorageFactory f)
			throws InterruptedException, ExecutionException {
		return journals != null ? journals//
				: (journals = new Database<>(f.open("journals keys").get()//
						, f.open("journals values").get()//
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
				));
	}

	@Provides
	@Named("userToJournalHistoryMap")
	protected IDatabase<String, Map<String, List<Boolean>>> userToJournalHistoryMapProvider(
			SuccececfulLineStorageFactory f) throws InterruptedException, ExecutionException {
		return userToJournalHistoryMap != null ? userToJournalHistoryMap//
				: (userToJournalHistoryMap = new Database<String, Map<String, List<Boolean>>>(//
						f.open("userToJournalHistoryMap keys").get()//
						, f.open("userToJournalHistoryMap values").get()//
						, new StringFactory()//
						, new MapFactory<>(s -> s, s -> s, s -> s.equals("t"), b -> b ? "t" : "")));
	}

	@Provides
	@Named("journalToUserHistoryMap")
	protected IDatabase<String, Map<String, List<Boolean>>> journalToUserHistoryMapProvider(
			SuccececfulLineStorageFactory f) throws InterruptedException, ExecutionException {
		return journalToUserHistoryMap != null ? journalToUserHistoryMap : //
				(journalToUserHistoryMap = new Database<String, Map<String, List<Boolean>>>(//
						f.open("journalToUserHistoryMap keys").get()//
						, f.open("journalToUserHistoryMap values").get()//
						, new StringFactory()//
						, new MapFactory<>(s -> s, s -> s, s -> s.equals("t"), b -> b ? "t" : "")));
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

@Singleton
class FakeFactoryProvider implements Provider<FutureLineStorageFactoryFake> {
	static FutureLineStorageFactoryFake ret = new FutureLineStorageFactoryFake();

	@Override
	public FutureLineStorageFactoryFake get() {
		return ret;
	}
	
}




