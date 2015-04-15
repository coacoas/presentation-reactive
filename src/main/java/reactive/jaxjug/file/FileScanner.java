package reactive.jaxjug.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import rx.Observable;

public class FileScanner {
	private final Path path;

	public FileScanner(Path path) {
		this.path = path;
	}

	public Observable<Path> tree() {
		return Observable.<Path>create(observer -> {
			System.out.println("Scanning " + path.toString());
			try {
				Files.walkFileTree(path, new FileVisitorSubscriber(path, observer));
			} catch (IOException e) {
				observer.onError(e);
			} finally {
				observer.onCompleted();
			}
		});
	}
}
