package com.eam.biblioteca.businessLayer.dto.libro;

import com.eam.biblioteca.businessLayer.dto.reserva.ReservaDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalle completo de un libro incluyendo historial de reservas")
public class LibroDetalleDTO {

    @Schema(description = "Datos generales del libro")
    private LibroDTO libro;

    @Schema(description = "Total histórico de reservas registradas para el libro", example = "5")
    private Integer totalReservas;

    @Schema(description = "Listado detallado de todas las reservas del libro")
    private List<ReservaDTO> reservas;
}
