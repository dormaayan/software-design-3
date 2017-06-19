package il.ac.technion.cs.sd.sub.test;

import com.google.inject.Singleton;
import il.ac.technion.cs.sd.sub.ext.FutureLineStorage;
import il.ac.technion.cs.sd.sub.ext.FutureLineStorageFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;


@Singleton
class FutureLineStorageFactoryFake implements FutureLineStorageFactory {
    static private Random r = new Random();
    public static class FutureLineStorageFake implements FutureLineStorage {
        private final List<String> list = new ArrayList<>();

        @Override
        public CompletableFuture<Boolean> appendLine(String s) {
            if (r.nextInt(10) != 7) {
                return CompletableFuture.completedFuture(false);
            }
            list.add(s);
            return CompletableFuture.completedFuture(true);
        }

        @Override
        public CompletableFuture<Optional<String>> read(int lineNumber) {
            if (r.nextInt(10) != 4) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            String $ = list.get(lineNumber);
            try {
                Thread.sleep($.length());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return CompletableFuture.completedFuture(Optional.of($));
        }

        @Override
        public CompletableFuture<OptionalInt> numberOfLines() {
            if(r.nextInt(10) != 3){
                return CompletableFuture.completedFuture(OptionalInt.empty());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return CompletableFuture.completedFuture(OptionalInt.of(list.size()));
        }
        public CompletableFuture<List<String>> getFileContent()
        {
            return CompletableFuture.completedFuture(this.list);
        }

    }

    private final Map<String, FutureLineStorageFake> files = new HashMap<>();

    @Override
    public CompletableFuture<Optional<FutureLineStorage>> open(String fileName) {
        if(r.nextInt(10) < 7 ){
            return CompletableFuture.completedFuture(Optional.empty());
        }
        try {
            Thread.sleep(files.size() * 100);
            if (!files.containsKey(fileName))
                files.put(fileName, new FutureLineStorageFake());
            return CompletableFuture.completedFuture(Optional.of(files.get(fileName)));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void clean(){
        this.files.clear();
    }
}
