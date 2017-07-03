import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import il.ac.technion.cs.sd.sub.app.SubscriberApp;
import il.ac.technion.cs.sd.sub.app.SubscriberInitializer;
import il.ac.technion.cs.sd.sub.app.SubscriberReader;
import il.ac.technion.cs.sd.sub.ext.FutureLineStorageFactory;

public class FakeSubscriberModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SubscriberInitializer.class).to(SubscriberApp.class);
        bind(SubscriberReader.class).to(SubscriberApp.class);
        bind(FutureLineStorageFactory.class).toProvider(FakeFactoryProvider.class);
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
