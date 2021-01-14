package io.github.eziomou.deepdota.data.opendota;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

final class OpenDota {

    private static final Logger log = LoggerFactory.getLogger(OpenDota.class);

    private static final URI BASE_URI = URI.create("https://api.opendota.com/api/explorer/");

    private final HttpClient client;

    private OpenDota() {
        this(HttpClient.newHttpClient());
    }

    private OpenDota(HttpClient client) {
        this.client = client;
    }

    public Single<String> explorer(String query) {
        return Single.fromCallable(() -> {
            URI uri = BASE_URI.resolve("?sql=" + encode(query));
            return createRequest(uri);
        }).flatMap(this::send);

    }

    private String encode(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    private HttpRequest createRequest(URI uri) {
        return HttpRequest.newBuilder(uri)
                .header("Accept", "application/json")
                .build();
    }

    private Single<String> send(HttpRequest request) {
        return Completable.fromRunnable(() -> log.info(request.toString()))
                .andThen(Single.fromFuture(client.sendAsync(request, HttpResponse.BodyHandlers.ofString())))
                .flatMap(response -> {
                    if (response.statusCode() == 200) {
                        return Single.just(response.body());
                    }
                    return Single.error(new Throwable(response.toString()));
                });
    }

    public static OpenDota newInstance() {
        return new OpenDota();
    }
}
