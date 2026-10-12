package com.baozistore.controller;

import com.baozistore.model.Cliente;
import com.baozistore.repository.ClienteRepository;
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
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @PostMapping
    public ResponseEntity<Cliente> criar(@Valid @RequestBody Cliente cliente) {
        cliente.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteRepository.save(cliente));
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @Valid @RequestBody Cliente dados) {
        Cliente cliente = buscarPorId(id);
        cliente.setNome(dados.getNome());
        cliente.setClienteDesde(dados.getClienteDesde());
        return clienteRepository.save(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
        clienteRepository.flush();
        return ResponseEntity.noContent().build();
    }
}
