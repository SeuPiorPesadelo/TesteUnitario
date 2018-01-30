package br.ce.wcaquino.servicos;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@Test
	public void teste() throws Exception{
		LocacaoService service = new LocacaoService();
		Usuario u = new Usuario("Lucas");
		Filme f = new Filme("Pelados em Santos", 1, 2.5);
		
		Locacao l = service.alugarFilme(u, f);

		Assert.assertEquals(2.5, l.getValor(), 0.01);

		//assertThat = verifique que...
		Assert.assertThat(l.getValor(), CoreMatchers.is(2.5));
		Assert.assertThat(l.getValor(), CoreMatchers.is(CoreMatchers.not(3.0)));
		
		Assert.assertTrue(DataUtils.isMesmaData(l.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(l.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	}
	
	@Test(expected=FilmeSemEstoqueException.class)
	public void testeLocacaoFilmeSemEstoque() throws Exception{
		LocacaoService service = new LocacaoService();
		Usuario u = new Usuario("Lucas");
		Filme f = new Filme("Pelados no Bairro", 0, 2.5);
		
		Locacao l = service.alugarFilme(u, f);

		Assert.assertEquals(2.5, l.getValor(), 0.01);

		//assertThat = verifique que...
		Assert.assertThat(l.getValor(), CoreMatchers.is(2.5));
		Assert.assertThat(l.getValor(), CoreMatchers.is(CoreMatchers.not(3.0)));
		
		Assert.assertTrue(DataUtils.isMesmaData(l.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(l.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	}
	
	@Test
	public void testeLocacaoFilmeSemEstoqueComTryCatch(){
		LocacaoService service = new LocacaoService();
		Usuario u = new Usuario("Lucas");
		Filme f = new Filme("Pelados no Bairro", 0, 2.5);
		
		try {
			service.alugarFilme(u, f);
		} catch (Exception e) {
			Assert.assertTrue(e.getClass().getSimpleName().equals(FilmeSemEstoqueException.class.getSimpleName()));
		}
	}
}
