package com.generation.farmacia.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.farmacia.dto.ProdutoRequestDTO;
import com.generation.farmacia.dto.ProdutoResponseDTO;
import com.generation.farmacia.model.Categoria;
import com.generation.farmacia.model.Produto;
import com.generation.farmacia.repository.CategoriaRepository;
import com.generation.farmacia.repository.ProdutoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Pesquisar todos os produtos
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> getAll() {
        List<ProdutoResponseDTO> dtos = produtoRepository.findAll()
                .stream()
                .map(ProdutoResponseDTO::new) // Usa o construtor do Record
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    // Pesquisar produto por Id
    @GetMapping("/{id}")
    public ResponseEntity<Produto> getById(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(resposta -> ResponseEntity.ok(resposta))
                .orElse(ResponseEntity.notFound().build());
    }

    // Pesquisa por quantidade em estoque
    @GetMapping("/estoque-minimo/{quantidade}")
    public ResponseEntity<List<Produto>> getByEstoqueMinimo(@PathVariable Integer quantidade) {
        return ResponseEntity.ok(produtoRepository.findAllByQuantidadeLessThanEqual(quantidade));
    }

    // Cadastrar produto
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> post(@Valid @RequestBody ProdutoRequestDTO request) {
        
        // Busca a categoria baseado no ID que veio no DTO
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada."));

        // Converte o DTO recebido para a Entidade de Banco
        Produto produto = new Produto();
        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setQuantidade(request.quantidade());
        produto.setCategoria(categoria);

        Produto produtoSalvo = produtoRepository.save(produto);

        // Devolve o DTO de resposta limpo
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProdutoResponseDTO(produtoSalvo));
    }

    // Atualizar produto
    @PutMapping
    public ResponseEntity<Produto> put(@Valid @RequestBody Produto produto) {
        // Valida se o produto existe
        if (!produtoRepository.existsById(produto.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Valida se a categoria associada existe
        if (produto.getCategoria() == null || produto.getCategoria().getId() == null || 
            !categoriaRepository.existsById(produto.getCategoria().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria inválida ou inexistente!");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(produtoRepository.save(produto));
    }

    // Excluir produto por ID
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);

        if (produto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado!");
        }

        produtoRepository.deleteById(id);
    }
}