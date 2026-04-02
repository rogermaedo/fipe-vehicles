package org.acme;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Ponto de entrada JVM (IDE ou {@code java -jar ...}). Fluxo usual: {@code ./mvnw quarkus:dev}.
 */
@QuarkusMain
public class Main {

    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
