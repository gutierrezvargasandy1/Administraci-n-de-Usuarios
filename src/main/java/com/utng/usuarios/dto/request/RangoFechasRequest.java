package com.utng.usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Bloque opcional para filtrar por rango de fecha de creacion dentro de la
 * busqueda dinamica. Cualquiera de los dos extremos puede omitirse: el criterio
 * se arma con los limites que realmente se reciban.
 */
@Schema(name = "RangoFechasRequest", description = "Limites del rango de fecha de creacion.")
public class RangoFechasRequest {

    @Schema(description = "Limite inferior inclusivo.", example = "2026-01-01T00:00:00")
    private LocalDateTime desde;

    @Schema(description = "Limite superior inclusivo.", example = "2026-12-31T23:59:59")
    private LocalDateTime hasta;

    public LocalDateTime getDesde() {
        return desde;
    }

    public void setDesde(LocalDateTime desde) {
        this.desde = desde;
    }

    public LocalDateTime getHasta() {
        return hasta;
    }

    public void setHasta(LocalDateTime hasta) {
        this.hasta = hasta;
    }
}
