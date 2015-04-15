package reactive.jaxjug.scheduling;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by bcarlson on 4/14/15.
 */
public class SchedulingExample {
    public static void main(String[] args) throws InterruptedException {
        Observable.range(0, 10).
                subscribeOn(Schedulers.computation()).
                map(i -> {
                    System.out.println("map1 => " + Thread.currentThread().getName() + ": " + i);
                    return i + 1;
                }).
                observeOn(Schedulers.newThread()).
                map(i -> {
                    System.out.println("map2 => " + Thread.currentThread().getName() + ": " + i);
                    return i * 10;
                }).
                observeOn(Schedulers.io()).
                subscribe(i ->
                                System.out.println("subscribe => " + Thread.currentThread().getName() + ": " + i)
                );

        Thread.sleep(10000);
    }
}
