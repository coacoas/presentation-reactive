package reactive.jaxjug;

import rx.Observable;

/**
 * Created by bcarlson on 4/12/15.
 */
public class Error {
    public static void main(String[] args) {

        Observable<Integer> errorStream = Observable.just(2, 1, 0, -1).
                map(i -> 4 / i);

        errorStream.subscribe(
                next -> System.out.println("Received " + next),
                exc -> System.out.println("Received exception: " + exc.getMessage()),
                () -> System.out.println("Completed"));
    }
}
