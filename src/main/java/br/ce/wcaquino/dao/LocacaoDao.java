package br.ce.wcaquino.dao;

import java.util.List;

import br.ce.wcaquino.entidades.Locacao;

public interface LocacaoDao {

	public void salvar(Locacao l);

	public List<Locacao> obterLocacoesPendentes();
}
