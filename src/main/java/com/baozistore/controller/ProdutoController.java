package com.baozistore.controller;

import com.baozistore.model.Produto;
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
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @PostMapping
    public ResponseEntity<Produto> criar(@Valid @RequestBody Produto produto) {
        produto.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoRepository.save(produto));
    }

    @GetMapping
    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable Long id, @Valid @RequestBody Produto dados) {
        Produto produto = buscarPorId(id);
        produto.setNome(dados.getNome());
        produto.setPreco(dados.getPreco());
        produto.setEstoque(dados.getEstoque());
        return produtoRepository.save(produto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        Produto produto = buscarPorId(id);
        produtoRepository.delete(produto);
        produtoRepository.flush();
        return ResponseEntity.noContent().build();
    }
}
