package com.buy01.tests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

public class BaseSpec {

        protected static RequestSpecification requestSpec;
        protected static ResponseSpecification responseSpec200;
        protected static ResponseSpecification responseSpec400;
        protected static ResponseSpecification responseSpec401;

        @BeforeAll
        static void setup() {
                RestAssured.useRelaxedHTTPSValidation();

                requestSpec = new RequestSpecBuilder()
                                .setBaseUri("https://localhost")
                                .setPort(10000)
                                .setContentType(ContentType.JSON)
                                .addFilter(new RequestLoggingFilter())
                                .addFilter(new ResponseLoggingFilter())
                                .build();

                responseSpec200 = new ResponseSpecBuilder()
                                .expectStatusCode(200)
                                .expectContentType(ContentType.JSON)
                                .build();

                responseSpec400 = new ResponseSpecBuilder()
                                .expectStatusCode(400)
                                .build();

                responseSpec401 = new ResponseSpecBuilder()
                                .expectStatusCode(401)
                                .build();
        }
}
