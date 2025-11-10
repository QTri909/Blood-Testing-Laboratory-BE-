package com.darkkernel.microservices.gateway.routes;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiRoutingConfig {

    /*
    + Webflux has 2 programming models:
    1. Annotated Controllers (similar to Spring MVC)
    2. Functional Endpoints (using RouterFunction and HandlerFunction)
    + Option 1: In API Gateway, we can use both of them but typically use Functional Endpoints to define routing rules.

    + Option 2: Define it using yml file.
    * */
}
