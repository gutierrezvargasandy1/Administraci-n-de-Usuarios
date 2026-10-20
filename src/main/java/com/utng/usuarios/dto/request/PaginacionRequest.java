package com.utng.usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Bloque de paginacion que viaja dentro del Request DTO de busqueda.
 *
 * Es un objeto OPCIONAL: cuando se omite, el servicio aplica los valores
 * configurados en app.paginacion. Pero si el cliente decide enviarlo, sus dos
 * campos son obligatorios, y por eso se anotan con @NotNull y no con @NotBlank:
 * se trata de valores numericos en los que el cero (primera pagina) es valido.
 */
@Schema(name = "PaginacionRequest", description = "Pagina y tamanio de pagina solicitados.")
public class PaginacionRequest {

    @Schema(description = "Numero de pagina, iniciando en cero.", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El numero de pagina es obligatorio cuando se envia el bloque de paginacion.")
    @Min(value = 0, message = "El numero de pagina no puede ser negativo.")
    private Integer pagina;

    @Schema(description = "Cantidad de registros por pagina.", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El tamanio de pagina es obligatorio cuando se envia el bloque de paginacion.")
    @Min(value = 1, message = "El tamanio de pagina debe ser de al menos 1 registro.")
    @Max(value = 50, message = "El tamanio de pagina no puede exceder 50 registros.")
    private Integer tamanio;

    public Integer getPagina() {
        return pagina;
    }

    public void setPagina(Integer pagina) {
        this.pagina = pagina;
    }

    public Integer getTamanio() {
        return tamanio;
    }

    public void setTamanio(Integer tamanio) {
        this.tamanio = tamanio;
    }
}
