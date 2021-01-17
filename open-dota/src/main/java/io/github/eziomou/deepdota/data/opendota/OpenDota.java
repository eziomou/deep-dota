package io.github.eziomou.deepdota.data.opendota;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

public final class OpenDota {

    private static final Logger log = LoggerFactory.getLogger(OpenDota.class);

    private static final URI BASE_URI = URI.create("https://api.opendota.com/api/explorer/");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> Observable<T> explorer(String query, Function<JsonNode, T> mapper) {
        return Single
                .fromCallable(() -> {
                    URI uri = BASE_URI.resolve("?sql=" + encode(query));
                    return createRequest(uri);
                })
                .flatMap(this::send)
                .flatMapObservable(json -> parseExplorerResponse(json, mapper));
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
                .andThen(Single.fromFuture(httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())))
                .flatMap(response -> {
                    if (response.statusCode() == 200) {
                        return Single.just(response.body());
                    }
                    return Single.error(new Throwable(response.toString()));
                });
    }

    private <T> Observable<T> parseExplorerResponse(String json, Function<JsonNode, T> mapper) {
        return Observable.create(emitter -> {
            JsonNode root = objectMapper.readTree(json);
            JsonNode rowCount = root.get("rowCount");
            JsonNode rows = root.get("rows");
            if (rowCount != null && rowCount.intValue() > 0 && rows != null) {
                rows.forEach(node -> emitter.onNext(mapper.apply(node)));
            }
            emitter.onComplete();
        });
    }

    public static OpenDota newInstance() {
        return new OpenDota();
    }

    public static <T> Maybe<T> last(List<? extends T> elements) {
        return elements.size() == 0 ? Maybe.empty() : Maybe.just(elements.get(elements.size() - 1));
    }
}
