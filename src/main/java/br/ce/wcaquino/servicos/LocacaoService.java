package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;

public class LocacaoService {
	
	private LocacaoDao dao;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException {
		double valoresFilmes = 0;
		for (Filme f : filmes) {
			if(f.getEstoque() == 0){
				throw new FilmeSemEstoqueException();
			}
			valoresFilmes += f.getPrecoLocacao();
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(valoresFilmes);

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//calcula desconto
		calculaDesconto(locacao);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}

	private void calculaDesconto(Locacao locacao) {
		if (locacao.getFilmes().size() == 3) {
			locacao.setValor(locacao.getValor() * .75); 
		} else if(locacao.getFilmes().size() == 4){
			locacao.setValor(locacao.getValor() * .5); 
		} else if(locacao.getFilmes().size() == 5){
			locacao.setValor(locacao.getValor() * .25); 
		} else if(locacao.getFilmes().size() >= 6){
			locacao.setValor(0.0);
		}
	}

	public void setLocacaoDao(LocacaoDao dao) {
		this.dao = dao;
	}
}