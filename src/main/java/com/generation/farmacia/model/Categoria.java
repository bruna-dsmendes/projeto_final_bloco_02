package com.generation.farmacia.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "É obrigatório inserir um nome!")
    @Size(min = 3, max = 100, message = "O atributo nome deve conter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "É obrigatório inserir uma descrição!")
    @Size(min = 5, max = 1000, message = "O atributo descrição deve conter entre 5 e 1000 caracteres")
    private String descricao;
    
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "categoria", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties("categoria")
    private List<Produto> produto;
 
    public Categoria() {}

    public Categoria(Long id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

	public List<Produto> getProduto() {
		return produto;
	}

	public void setProduto(List<Produto> produto) {
		this.produto = produto;
	}
    
    
}
