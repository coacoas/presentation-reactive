package reactive.jaxjug;

import rx.Observable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by bcarlson on 4/12/15.
 */
public class FutureObservable {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService svc = Executors.newCachedThreadPool();

        Observable<String> future = Observable.from(svc.submit(() -> {
            Thread.sleep(3000);
            return "Future completed";
        }));

        future.map(String::toUpperCase).subscribe(System.out::println);

        svc.shutdown();
        svc.awaitTermination(30, SECONDS);
    }
}
