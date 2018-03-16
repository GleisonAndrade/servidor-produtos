package br.ufpi.webservice.servidor.dao;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import br.ufpi.carrinhoCompras.model.Produto;

@Stateless
public class ProdutoDao implements Serializable{

		
	private static final long serialVersionUID = 1L;
	
	@PersistenceContext
	private EntityManager em;
	
	public void salvar(Produto produto){
		em.merge(produto);
	}
	
	public void remover(Produto produto){
		em.remove(produto);
	}
	
	public List<Produto> listar(){
		TypedQuery<Produto> query = em.createQuery("Select p from Produto p", Produto.class);
		return query.getResultList();
	}
}
