package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Filme;

public class FilmeBuilder {

	private Filme filme;
	
	private FilmeBuilder(){}
	
	public static FilmeBuilder umFilme(){
		FilmeBuilder fb = new FilmeBuilder();
		fb.filme = new Filme();
		fb.filme.setEstoque(2);
		fb.filme.setNome("Nome Qualquer");
		fb.filme.setPrecoLocacao(5.0);
		return fb;
	}
	
	public Filme agora(){
		return filme;
	}
	
	public FilmeBuilder semEstoque(){
		filme.setEstoque(0);
		return this;
	}
	
	public FilmeBuilder comValor(Double valor){
		filme.setPrecoLocacao(valor);
		return this;
	}
}
