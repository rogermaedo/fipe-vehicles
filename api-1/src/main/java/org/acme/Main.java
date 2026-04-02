package org.acme;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Ponto de entrada JVM (IDE ou {@code java -jar ...}). Em Quarkus o fluxo usual continua sendo
 * {@code ./mvnw quarkus:dev}; esta classe só torna explícito o {@code main} para “Run” no IntelliJ/Eclipse.
 */
@QuarkusMain
public class Main {

    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
