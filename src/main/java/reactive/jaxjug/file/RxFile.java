package reactive.jaxjug.file;

import java.io.File;
import java.nio.file.Path;

import rx.Observable;

public class RxFile {
	private static String threadName() {
		return Thread.currentThread().getName();
	}

	public static void main(String[] args) throws InterruptedException {
		File dir = new File("data/root");

		Observable<Path> scanner = new FileScanner(dir.toPath()).tree();

		scanner.subscribe(System.out::println);

		System.out.println("Done");
	}
}
