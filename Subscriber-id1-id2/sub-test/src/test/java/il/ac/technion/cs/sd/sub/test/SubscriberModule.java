package il.ac.technion.cs.sd.sub.test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.google.common.base.Function;
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

// This module is in the testing project, so that it could easily bind all dependencies from all levels.
public class SubscriberModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(SubscriberInitializer.class).to(SubscriberInitializerImpl.class);
		bind(SubscriberReader.class).to(SubscriberReaderImpl.class);
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

	public class ListFactory<T> implements IStringableFactory<List<T>> {
		final Function<String, T> parser;
		final Function<T, String> serializer;

		public ListFactory(Function<String, T> parser, Function<T, String> serializer) {
			this.parser = parser;
			this.serializer = serializer;
		}

		@Override
		public CompletableFuture<List<T>> createObject(CompletableFuture<String> s) {
			return s.thenApply(str -> Arrays.asList(str.split(";"))//
					.stream().map(string -> parser.apply(string)).collect(Collectors.toList()));
		}

		@Override
		public CompletableFuture<String> createString(CompletableFuture<List<T>> e) {
			return e.thenApply(lst -> lst.stream().map(t -> serializer.apply(t))//
					.reduce("", (s1, s2) -> s1 + ";" + s2));
		}

	}

	@Provides
	@Named("userToJournals")
	protected IDatabase<String, List<JournalRegistration>> userToJournalsProvider(SuccececfulLineStorageFactory f)
			throws InterruptedException, ExecutionException {
		return new Database<String, List<JournalRegistration>>(f.open("userToJournals keys").get(),
				f.open("userToJournals values").get(), //
				new StringFactory(), new ListFactory<>(JournalRegistration::parse, JournalRegistration::serialize));
	}

	@Provides
	@Named("journals")
	protected IDatabase<String, JournalInfo> journalsProvider(SuccececfulLineStorageFactory f)
			throws InterruptedException, ExecutionException {
		return new Database<String, JournalInfo>(f.open("userToJournals keys").get(),
				f.open("userToJournals values").get(), //
				new StringFactory(), new IStringableFactory<JournalInfo>() {
					@Override
					public CompletableFuture<JournalInfo> createObject(CompletableFuture<String> s) {
						return s.thenApply(JournalInfo::parse);
					}
					@Override
					public CompletableFuture<String> createString(CompletableFuture<JournalInfo> e) {
						return e.thenApply(JournalInfo::serialize);
					}
				});
	}

}
