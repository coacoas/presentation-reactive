package reactive.jaxjug;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.reactivex.netty.protocol.http.server.HttpServer;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Created by bcarlson on 4/12/15.
 */

public class HTTP {
    public static void main(String[] args) throws InterruptedException {
        HttpServer<ByteBuf, ByteBuf> server =
                RxNetty.createHttpServer(8181, (request, response) -> {
                    System.out.println("Server => Request: " + request.getPath());
                    switch (request.getPath()) {
                        case "/error":
                            System.err.println("Server => Error [" + request.getPath() + "]");
                            response.setStatus(HttpResponseStatus.BAD_REQUEST);
                            response.writeString("Error 500: Bad Request\n");
                            break;
                        default:
                            response.setStatus(OK);
                            response.writeString("Requested: " + request.getPath());
                            break;
                    }
                    return response.close();
                });

        server.start();


        RxNetty.createHttpGet("http://localhost:8181/test").
                flatMap(HttpClientResponse::getContent).
                map(data -> "Client => " + data.toString(Charset.defaultCharset())).
                toBlocking().forEach(System.out::println);

        RxNetty.createHttpGet("http://localhost:8181/error").
                flatMap(HttpClientResponse::getContent).
                map(data -> "Client => " + data.toString(Charset.defaultCharset())).
                toBlocking().forEach(System.out::println);

        server.shutdown();
    }
}
