package reactive.jaxjug.http;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

public class HttpExample {
	public static void main(String[] args) throws InterruptedException, URISyntaxException {
		HttpServer server = RxNetty.createHttpServer(0, (request, response) -> {
			try {
				System.out.println("Server => received " + request.getPath());
				switch (request.getPath()) {
					case "/headers":
						response.setStatus(OK);
						response.writeString("Received request with headers :\n");
						for (Map.Entry<String, String> header : response.getHeaders().entries()) {
							response.writeString("\t" + header.getKey() + ": " + header.getValue() + "\n");
						}
						response.writeString("\tHost: " + response.getHeaders().getHost("Unknown"));
						break;
					case "/time":
						response.setStatus(OK);
						DataHolder current = new DataHolder("CurrentTime",
								Long.toString(System.currentTimeMillis()));
						response.writeString(new ObjectMapper().writeValueAsString(current));
						break;
					default:
						response.setStatus(NOT_FOUND);
						response.writeString("Could not find path " + request.getPath());
						break;
				}
			} catch (JsonProcessingException e) {
				response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
				response.writeString(e.getMessage());
			}

			return response.close();
		});

		server.start();
		int port = server.getServerPort();
		System.out.println("Server started on port " + port);

		URI uri = new URI("http", null, "localhost", port, null, null, null);

		retrieve(uri.resolve("/err")).subscribe(
				data -> System.out.println("Server time: " + data),
				e -> System.out.println("Client (/err) => " + e.getMessage()),
				() -> System.out.println("Client (/err) => Done retrieving data"));

		retrieve(uri.resolve("/headers")).subscribe(data -> System.out.println("Client (/headers) => " + data));

		retrieve(uri.resolve("/time")).
				flatMap(str -> Observable.<DataHolder>create(subscriber -> {
					try {
						subscriber.onNext(new ObjectMapper().readValue(str, DataHolder.class));
						subscriber.onCompleted();
					} catch (Exception e) {
						subscriber.onError(e);
					}
				})).
				toBlocking().
				forEach(holder ->
						System.out.println("Client (/time) => Server time: " + holder.value));

		server.shutdown();
	}

	public static Observable<String> retrieve(URI uri) {
		return RxNetty.createHttpGet(uri.toString()).
				flatMap(response -> {
					if (response.getStatus().equals(OK)) {
						return response.getContent();
					} else {
						return response.getContent().
								map(c -> c.toString(Charset.defaultCharset())).
								map(err -> response.getStatus().equals(NOT_FOUND) ?
										new NoSuchElementException(err) :
										new RuntimeException(err)).
								flatMap(Observable::error);
					}
				}).
				map(buff -> buff.toString(Charset.defaultCharset()));
	}

	private static class DataHolder {
		public String key;
		public String value;

		@JsonCreator
		public DataHolder(@JsonProperty("key") String key,
						  @JsonProperty("value") String value) {
			this.key = key;
			this.value = value;
		}
	}

}


