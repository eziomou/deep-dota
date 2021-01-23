package io.github.eziomou.deepdota.webapi;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
class PredictionRouter {

    private final PredictionHandler predictionHandler;

    public PredictionRouter(PredictionHandler predictionHandler) {
        this.predictionHandler = predictionHandler;
    }

    @Bean
    RouterFunction<ServerResponse> predict() {
        return route(GET("/prediction"), predictionHandler::predict);
    }
}
