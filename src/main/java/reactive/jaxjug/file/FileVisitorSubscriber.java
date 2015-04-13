package reactive.jaxjug.file;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import rx.Subscriber;

public class FileVisitorSubscriber extends SimpleFileVisitor<Path> {
	private final Subscriber<? super Path> subscriber;
	private final Path root;

	public FileVisitorSubscriber(Path root, Subscriber<? super Path> s) {
		this.root = root;
		this.subscriber = s;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		subscriber.onNext(file);
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		subscriber.onError(exc);
		return super.visitFileFailed(file, exc);
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		subscriber.onNext(dir);
		return super.preVisitDirectory(dir, attrs);
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		if (exc != null) {
			subscriber.onError(exc);
		}
		if (dir.equals(root)) {
			subscriber.onCompleted();
		}
		return super.postVisitDirectory(dir, exc);
	}
}
