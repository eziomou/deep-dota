package io.github.eziomou.deepdota.webapi;

import io.github.eziomou.data.Hero;
import io.github.eziomou.predict.Prediction;
import io.github.eziomou.predict.Predictor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class PredictionHandler {

    private static final Map<Integer, Hero> HEROES;

    static {
        HEROES = new HashMap<>();
        Arrays.stream(Hero.values()).forEach(hero -> HEROES.put(hero.getId(), hero));
    }

    private final Predictor predictor;

    public PredictionHandler(Predictor predictor) {
        this.predictor = predictor;
    }

    public Mono<ServerResponse> predict(ServerRequest request) {
        return predict(request.queryParam("radiant").map(this::extractHeroes).orElse(Collections.emptyList()),
                request.queryParam("dire").map(this::extractHeroes).orElse(Collections.emptyList()))
                .flatMap(this::map);
    }

    private List<Hero> extractHeroes(String string) {
        List<Hero> heroes = new ArrayList<>();
        String[] parts = string.isBlank() ? new String[0] : string.split(",");
        for (String part : parts) {
            try {
                int id = Integer.parseInt(part);
                if (HEROES.containsKey(id)) {
                    heroes.add(HEROES.get(id));
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hero id: " + part);
            }
        }
        return heroes.size() < 5 ? heroes : heroes.subList(0, 5);
    }

    private Mono<Prediction> predict(List<Hero> radiant, List<Hero> dire) {
        return Mono.from(predictor.predict(radiant, dire).toFlowable());
    }

    private Mono<ServerResponse> map(Prediction prediction) {
        return ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(prediction));
    }
}
