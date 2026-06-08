package com.generation.farmacia.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.farmacia.model.Carrinho;
import com.generation.farmacia.model.ItemCarrinho;
import com.generation.farmacia.model.Produto;
import com.generation.farmacia.model.Usuario;
import com.generation.farmacia.repository.CarrinhoRepository;
import com.generation.farmacia.repository.ItemCarrinhoRepository;
import com.generation.farmacia.repository.ProdutoRepository;
import com.generation.farmacia.repository.UsuarioRepository;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método auxiliar para pegar o usuário autenticado no Token JWT corrente
    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsuario(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    // Retorna o carrinho do usuário ou cria um vazio se ele ainda não tiver nenhum
    public Carrinho buscarOuCriarCarrinho() {
        Usuario usuario = getUsuarioLogado();
        return carrinhoRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Carrinho novoCarrinho = new Carrinho();
                    novoCarrinho.setUsuario(usuario);
                    return carrinhoRepository.save(novoCarrinho);
                });
    }

    // Regra de negócio principal: Adiciona produto ou quantidade
    public Carrinho adicionarProduto(Long produtoId, Integer quantidade) {
        Carrinho carrinho = buscarOuCriarCarrinho();
        
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));


        // Verifica se o produto já está na lista do carrinho
        Optional<ItemCarrinho> itemExistente = carrinho.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(produtoId))
                .findFirst();

        if (itemExistente.isPresent()) {
            // Se já existe, apenas aumenta a quantidade simulação
            ItemCarrinho item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + quantidade);
        } else {
            // Se é um produto novo no carrinho, cria um novo ItemCarrinho
            ItemCarrinho novoItem = new ItemCarrinho();
            novoItem.setProduto(produto);
            novoItem.setQuantidade(quantidade);
            novoItem.setCarrinho(carrinho);
            carrinho.getItens().add(novoItem);
        }

        return carrinhoRepository.save(carrinho);
    }

    // Limpa ou remove um item específico da simulação
    public Carrinho removerProduto(Long itemId) {
        Carrinho carrinho = buscarOuCriarCarrinho();
        
        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado no carrinho."));

        // Garante que o item pertence mesmo ao carrinho do usuário logado antes de apagar
        if (!item.getCarrinho().getId().equals(carrinho.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ação não permitida.");
        }

        carrinho.getItens().remove(item);
        itemCarrinhoRepository.delete(item);
        
        return carrinhoRepository.save(carrinho);
    }
}