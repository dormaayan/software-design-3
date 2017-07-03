package library;

import com.google.inject.AbstractModule;

import il.ac.technion.cs.sd.sub.ext.FutureLineStorage;
import il.ac.technion.cs.sd.sub.ext.FutureLineStorageFactory;

public class FakeLineStorageMoudle extends AbstractModule {

	@Override
	protected void configure() {
		bind(FutureLineStorage.class).to(TestStorer.class);
		bind(FutureLineStorageFactory.class).toInstance(new TestStorerFactory());
	}

}
