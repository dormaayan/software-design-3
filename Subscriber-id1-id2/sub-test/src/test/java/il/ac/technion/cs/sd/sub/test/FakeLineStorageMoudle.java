package il.ac.technion.cs.sd.sub.test;

import com.google.inject.AbstractModule;

import il.ac.technion.cs.sd.sub.ext.FutureLineStorageFactory;

public class FakeLineStorageMoudle extends AbstractModule {
	@Override
	protected void configure() {
		bind(FutureLineStorageFactory.class).toProvider(FakeFactoryProvider.class);
	}
}