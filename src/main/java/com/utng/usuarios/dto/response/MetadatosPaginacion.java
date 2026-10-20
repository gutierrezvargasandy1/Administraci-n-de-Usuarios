package com.utng.usuarios.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Metadatos de paginacion que acompanian a los listados.
 */
@Schema(name = "MetadatosPaginacion", description = "Informacion de la pagina devuelta.")
public class MetadatosPaginacion {

    @Schema(description = "Numero de pagina actual, iniciando en cero.", example = "0")
    private int pagina;

    @Schema(description = "Cantidad de registros solicitados por pagina.", example = "10")
    private int tamanio;

    @Schema(description = "Registros incluidos en esta pagina.", example = "10")
    private int elementosEnPagina;

    @Schema(description = "Total de registros que cumplen los criterios.", example = "35")
    private long totalElementos;

    @Schema(description = "Total de paginas disponibles.", example = "4")
    private int totalPaginas;

    @Schema(description = "Indica si esta es la primera pagina.", example = "true")
    private boolean primera;

    @Schema(description = "Indica si esta es la ultima pagina.", example = "false")
    private boolean ultima;

    public MetadatosPaginacion() {
    }

    public MetadatosPaginacion(int pagina, int tamanio, int elementosEnPagina,
                               long totalElementos, int totalPaginas, boolean primera, boolean ultima) {
        this.pagina = pagina;
        this.tamanio = tamanio;
        this.elementosEnPagina = elementosEnPagina;
        this.totalElementos = totalElementos;
        this.totalPaginas = totalPaginas;
        this.primera = primera;
        this.ultima = ultima;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void setTamanio(int tamanio) {
        this.tamanio = tamanio;
    }

    public int getElementosEnPagina() {
        return elementosEnPagina;
    }

    public void setElementosEnPagina(int elementosEnPagina) {
        this.elementosEnPagina = elementosEnPagina;
    }

    public long getTotalElementos() {
        return totalElementos;
    }

    public void setTotalElementos(long totalElementos) {
        this.totalElementos = totalElementos;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(int totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    public boolean isPrimera() {
        return primera;
    }

    public void setPrimera(boolean primera) {
        this.primera = primera;
    }

    public boolean isUltima() {
        return ultima;
    }

    public void setUltima(boolean ultima) {
        this.ultima = ultima;
    }
}
