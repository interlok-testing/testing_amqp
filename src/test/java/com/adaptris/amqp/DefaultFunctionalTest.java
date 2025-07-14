package com.adaptris.amqp;

import com.adaptris.testing.DockerComposeFunctionalTest;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.io.File;
import java.net.InetSocketAddress;
import java.time.Duration;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;

public class DefaultFunctionalTest extends DockerComposeFunctionalTest {

    protected static String INTERLOK_SERVICE_NAME = "interlok-1";
    protected static String RABBITMQ_SERVICE_NAME = "rabbitmq-1";
    protected static int INTERLOK_PORT = 8080;
    protected static int RABBITMQ_ADMIN_PORT = 15672;
    protected static int RABBITMQ_PORT = 5672;
    protected static WaitStrategy defaultWaitStrategy = Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30));

    @Override
    protected ComposeContainer setupContainers() throws Exception {
        return new ComposeContainer(new File("docker-compose.yaml"))
                .withExposedService(INTERLOK_SERVICE_NAME, INTERLOK_PORT, defaultWaitStrategy)
                .withExposedService(RABBITMQ_SERVICE_NAME, RABBITMQ_ADMIN_PORT)
                .withExposedService(RABBITMQ_SERVICE_NAME, RABBITMQ_PORT);
    }

    protected String getAdminEndpoint(String path) {
        InetSocketAddress address = getHostAddressForService(RABBITMQ_SERVICE_NAME, RABBITMQ_ADMIN_PORT);
        if (!path.startsWith("/")) path = "/" + path;
        return "http://" + address.getHostString() + ":" + address.getPort() + path;
    }

    @Test
    public void test() throws Exception {
        given().auth().basic("guest", "guest")
            .put(getAdminEndpoint("/api/vhosts/test"))
                .then().assertThat()
                .statusCode(201);
        Thread.sleep(60000);
        given().auth().basic("guest", "guest")
                .get(getAdminEndpoint("/api/queues/test"))
                .then().assertThat()
                .body("size()", is(2))
                .and().body("[0].message_stats.deliver", Matchers.greaterThan(0))
                .and().body("[1].messages", Matchers.greaterThan(0));
    }
}
