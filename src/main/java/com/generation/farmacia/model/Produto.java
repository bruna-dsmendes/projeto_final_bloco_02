package com.generation.farmacia.model;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "É obrigatório inserir nome do produto!")
    @Size(min = 2, max = 100, message = "O atributo nome deve conter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "É obrigatório inserir a descrição do produto!")
    @Size(min = 5, max = 1000, message = "O atributo descrição deve conter entre 5 e 1000 caracteres")
    private String descricao;

    @NotNull(message = "É obrigatório inserir a quantidade em estoque do produto!")
    private Integer quantidade;

    @NotNull(message = "É obrigatório inserir o preço do produto!")
    @Positive(message = "O preço deve ser maior do que zero!")
    private BigDecimal preco;

  
    @ManyToOne
    @JsonIgnoreProperties("produto")
    private Categoria categoria;

    public Produto() {}

    public Produto(Long id, String nome, String descricao, Integer quantidade, BigDecimal preco, Categoria categoria) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.preco = preco;
        this.categoria = categoria;
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

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

 
}
     