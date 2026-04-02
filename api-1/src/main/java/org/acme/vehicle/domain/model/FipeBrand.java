package org.acme.vehicle.domain.model;

/**
 * Marca como retornada pela FIPE ({@code Marca.codigo} / {@code Marca.nome}), antes da persistência interna.
 */
public record FipeBrand(String fipeCode, String name) {}
