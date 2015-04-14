package reactive.jaxjug;

import rx.Observable;

/**
 * Created by bcarlson on 4/14/15.
 */
public class Scratchpad {
    public static void main(String[] args) {
        Observable.just(3, 2, 1).
                map(i -> i * i).
                subscribe(
                        item -> System.out.println("Received: " + item),
                        err -> System.err.println("Error => " + err.getMessage()),
                        () -> System.out.println("Completed!"));

    }
}
