package reactive.jaxjug;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by bcarlson on 4/12/15.
 */
public class Intro {
    static ExecutorService executor = Executors.newCachedThreadPool();
    static Scheduler sch = Schedulers.from(executor);

    public static void main(String[] args) throws InterruptedException {
        Observable<String> langs = Observable.just("Java", "Scala", "Clojure", "Kotlin");

        langs.subscribe(System.out::println);

        Observable.from(executor.submit(() -> {
            Thread.sleep(3000);
            return "Future completed";
        })).subscribe(System.out::println);

        Observable<Long> ticks = Observable.interval(500, MILLISECONDS);

        langs.zipWith(ticks, (lang, tick) -> lang).
                toBlocking().
                forEach(System.out::println);

        Observable<String> fizzbuzz = ticks.
                map(i -> {
                    if (i % 5 == 0 && i % 3 == 0) {
                        return "FizzBuzz";
                    } else if (i % 3 == 0) {
                        return "Fizz";
                    } else if (i % 5 == 0) {
                        return "Buzz";
                    } else {
                        return Long.toString(i);
                    }
                });

        fizzbuzz.take(100).toBlocking().forEach(System.out::println);

        executor.shutdown();
        executor.awaitTermination(30, SECONDS);
    }
}
