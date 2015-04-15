package reactive.jaxjug.file;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import rx.Subscriber;

public class FileVisitorSubscriber implements FileVisitor<Path> {
	private final Subscriber<? super Path> subscriber;
	private final Path root;

	public FileVisitorSubscriber(Path root, Subscriber<? super Path> s) {
		this.root = root;
		this.subscriber = s;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if (subscriber.isUnsubscribed()) {
			return TERMINATE;
		} else {
			subscriber.onNext(file);
			return CONTINUE;
		}
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		if (!subscriber.isUnsubscribed()) {
			subscriber.onError(exc);
		}
		return TERMINATE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		if (subscriber.isUnsubscribed()) {
			return TERMINATE;
		} else {
			subscriber.onNext(dir);
			return CONTINUE;
		}
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		if (subscriber.isUnsubscribed()) {
			return TERMINATE;
		} else {
			if (exc != null) {
				subscriber.onError(exc);
			}
			if (dir.equals(root)) {
				subscriber.onCompleted();
			}
			return CONTINUE;
		}
	}
}
