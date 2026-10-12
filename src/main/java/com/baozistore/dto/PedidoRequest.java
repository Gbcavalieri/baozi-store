package com.baozistore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Corpo JSON recebido em POST/PUT /pedidos. */
public record PedidoRequest(
        @NotNull Long clienteId,
        @NotNull Long produtoId,
        @NotNull @Min(1) Integer quantidade
) {
}
