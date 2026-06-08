package com.generation.farmacia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.generation.farmacia.model.Carrinho;
import com.generation.farmacia.service.CarrinhoService;

@RestController
@RequestMapping("/carrinho")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    // Visualizar o carrinho do usuário atual
    @GetMapping
    public ResponseEntity<Carrinho> getCarrinho() {
        return ResponseEntity.ok(carrinhoService.buscarOuCriarCarrinho());
    }

    // Adicionar produto
    @PostMapping("/adicionar")
    public ResponseEntity<Carrinho> adicionarProduto(
            @RequestParam Long produtoId, 
            @RequestParam Integer quantidade) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carrinhoService.adicionarProduto(produtoId, quantidade));
    }

    // Excluir um item específico pelo ID do item
    @DeleteMapping("/remover/{id}")
    public ResponseEntity<Carrinho> removerProduto(@PathVariable Long id) {
        return ResponseEntity.ok(carrinhoService.removerProduto(id));
    }
}