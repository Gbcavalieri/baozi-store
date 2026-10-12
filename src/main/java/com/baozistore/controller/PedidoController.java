package com.baozistore.controller;

import com.baozistore.dto.PedidoRequest;
import com.baozistore.model.Cliente;
import com.baozistore.model.Pedido;
import com.baozistore.model.Produto;
import com.baozistore.repository.ClienteRepository;
import com.baozistore.repository.PedidoRepository;
import com.baozistore.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @PostMapping
    public ResponseEntity<Pedido> criar(@Valid @RequestBody PedidoRequest request) {
        Pedido pedido = new Pedido();
        preencher(pedido, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoRepository.save(pedido));
    }

    @GetMapping
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    @PutMapping("/{id}")
    public Pedido atualizar(@PathVariable Long id, @Valid @RequestBody PedidoRequest request) {
        Pedido pedido = buscarPorId(id);
        preencher(pedido, request);
        return pedidoRepository.save(pedido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        Pedido pedido = buscarPorId(id);
        pedidoRepository.delete(pedido);
        return ResponseEntity.noContent().build();
    }

    private void preencher(Pedido pedido, PedidoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente informado não existe"));
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto informado não existe"));
        pedido.setCliente(cliente);
        pedido.setProduto(produto);
        pedido.setQuantidade(request.quantidade());
    }
}
