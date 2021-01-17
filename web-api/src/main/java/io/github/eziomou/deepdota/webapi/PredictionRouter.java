package io.github.eziomou.deepdota.webapi;

import io.github.eziomou.data.Hero;
import io.github.eziomou.predict.Predictor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
class PredictionRouter {

    private static final Map<Integer, Hero> HEROES;

    static {
        HEROES = new HashMap<>();
        Arrays.stream(Hero.values()).forEach(hero -> HEROES.put(hero.getId(), hero));
    }

    @Bean
    RouterFunction<ServerResponse> predict(Predictor predictor) {
        return route(POST("/predict"), serverRequest -> predict(predictor, serverRequest));
    }

    private Mono<ServerResponse> predict(Predictor service, ServerRequest request) {
        return request.bodyToMono(PredictionRequest.class)
                .flatMap(p -> Mono.from(service.predict(map(p.getRadiant()), map(p.getDire())).toFlowable()))
                .flatMap(prediction -> ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(prediction)));
    }

    private List<Hero> map(List<Integer> heroes) {
        return heroes.stream().map(HEROES::get).collect(Collectors.toList());
    }
}
