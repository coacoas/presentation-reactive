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
		return Observable.<Path>create(s -> {
			System.out.println("Scanning");
			try {
				Files.walkFileTree(path, new FileVisitorSubscriber(path, s));
			} catch (IOException e) {
				s.onError(e);
			} finally {
				s.onCompleted();
			}
		});
	}
}
