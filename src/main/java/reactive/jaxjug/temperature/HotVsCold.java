package reactive.jaxjug.temperature;

import rx.Observable;
import rx.observables.ConnectableObservable;

import static java.util.concurrent.TimeUnit.SECONDS;

public class HotVsCold {
	public void cold() {
		System.out.println("COLD");
		Observable<Integer> ints = Observable.just(1,2,3);
		System.out.println("First");
		ints.subscribe(System.out::println, System.err::println, () -> System.out.println("Completed first"));
		System.out.println("Second");
		ints.subscribe(System.out::println, System.err::println, () -> System.out.println("Completed second"));
	}

	public void hot() throws InterruptedException {
		System.out.println("HOT");
		ConnectableObservable<Long> ints = Observable.interval(1, SECONDS).take(5).publish();
		System.out.println("First");
		ints.subscribe(System.out::println, System.err::println, () -> System.out.println("Completed first"));
		ints.connect();
		Thread.sleep(3000);
		System.out.println("Second");
		ints.subscribe(System.out::println, System.err::println, () -> System.out.println("Completed second"));
		Thread.sleep(5000);
	}

	public static void main(String[] args) throws InterruptedException {
		new HotVsCold().cold();
		new HotVsCold().hot();
	}
}
