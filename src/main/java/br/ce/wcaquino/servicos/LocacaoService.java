package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;

import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocacaoException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDao dao;
	private SPCService spcService;
	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocacaoException {
		double valoresFilmes = 0;
		for (Filme f : filmes) {
			if(f.getEstoque() == 0){
				throw new FilmeSemEstoqueException();
			}
			valoresFilmes += f.getPrecoLocacao();
		}

		boolean negativado = false;
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocacaoException("Problemas com SPC, tente novamente");
		}
		
		if(negativado){
			throw new LocacaoException("Usuario Negativado");
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
	
	public void notificarAtrasos(){
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for (Locacao locacao : locacoes) {
			if(locacao.getDataRetorno().before(new Date())){
				emailService.notificarAtrasos(locacao.getUsuario());
			}
		}
	}
	
	/**
	 * Pega uma locacao existente a cria uma nova
	 * 
	 * @param l
	 * @param dias
	 */
	public void prorrogarLocacao(Locacao l, int dias){
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(l.getUsuario());
		novaLocacao.setFilmes(l.getFilmes());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(l.getValor() * dias);
		dao.salvar(novaLocacao);
	}

	//nao precisa mais dos Set's pq o MockitoAnnotations.initMocks(this);
	//já está injetando os mocks
//	public void setLocacaoDao(LocacaoDao dao) {
//		this.dao = dao;
//	}
//	public void setSPCService(SPCService spc) {
//		this.spcService = spc;
//	}
//	public void setEmailService(EmailService emailService) {
//		this.emailService = emailService;
//	}
	
}