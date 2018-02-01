package br.ce.wcaquino.servicos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocacaoException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService service;
	private LocacaoDao dao;
	private SPCService spcService;
	private Usuario u;
	
	//JUnit soh zera variaveis de instancia
	private static int count;

	@BeforeClass //tem q ser static
	public static void initClass(){
		System.out.println("Before Class");
	}
	
	@AfterClass //tem q ser static
	public static void endClass(){
		System.out.println("After Class");
	}
	
	@Before
	public void init(){
		service	= new LocacaoService();
		u = UsuarioBuilder.umUsuario().agora();
		count++;
		System.out.println(count);
		
		//injeta os Mocks
		dao = Mockito.mock(LocacaoDao.class);
		service.setLocacaoDao(dao);
		
		spcService = Mockito.mock(SPCService.class);
		service.setSPCService(spcService);
	}
	
	@After
	public void end(){
		System.out.println("After");
	}
	
	@Test
	public void teste() throws FilmeSemEstoqueException, LocacaoException{
		Filme f = FilmeBuilder.umFilme().agora();
		Filme f1 = FilmeBuilder.umFilme().agora();
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(f);
		filmes.add(f1);
		
		Locacao l = service.alugarFilme(u, filmes);

		Assert.assertEquals(10.0, l.getValor(), 0.01);

		//assertThat = verifique que...
		Assert.assertThat(l.getValor(), CoreMatchers.is(10.0));
		Assert.assertThat(l.getValor(), CoreMatchers.is(CoreMatchers.not(5.0)));
		
		Assert.assertTrue(DataUtils.isMesmaData(l.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(l.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	}
	
	@Test(expected=FilmeSemEstoqueException.class)
	public void testeLocacaoFilmeSemEstoque() throws Exception{
		Filme f = FilmeBuilder.umFilme().semEstoque().agora();
		Filme f1 = new Filme("Maremoto no Saara", 1, 2.6);
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(f);
		filmes.add(f1);
		
		Locacao l = service.alugarFilme(u, filmes);

		Assert.assertEquals(2.5, l.getValor(), 0.01);

		//assertThat = verifique que...
		Assert.assertThat(l.getValor(), CoreMatchers.is(2.5));
		Assert.assertThat(l.getValor(), CoreMatchers.is(CoreMatchers.not(3.0)));
		
		Assert.assertTrue(DataUtils.isMesmaData(l.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(l.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	}
	
	@Test
	public void testeLocacaoFilmeSemEstoqueComTryCatch(){
		Filme f = FilmeBuilder.umFilme().semEstoque().agora();
		Filme f1 = new Filme("Maremoto no Saara", 1, 2.6);
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(f);
		filmes.add(f1);
		
		try {
			service.alugarFilme(u, filmes);
		} catch (Exception e) {
			Assert.assertTrue(e.getClass().getSimpleName().equals(FilmeSemEstoqueException.class.getSimpleName()));
		}
	}
	
	@Test
	public void testeDescontos25PorCento() throws FilmeSemEstoqueException, LocacaoException{
		Filme f = FilmeBuilder.umFilme().comValor(5.0).agora();
		Filme f1 = FilmeBuilder.umFilme().comValor(3.0).agora();
		Filme f2 = new Filme("Tempestade de Areia em Alto Mar", 1, 2.0);
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(f);
		filmes.add(f1);
		filmes.add(f2);
		Locacao l = service.alugarFilme(u, filmes);
		//desconto de 25%
		Assert.assertEquals(7.5, l.getValor(), 0.001);
	}
	
	@Test
	public void testeDescontos50PorCento() throws FilmeSemEstoqueException, LocacaoException{
		Filme f = FilmeBuilder.umFilme().comValor(2.5).agora();
		Filme f1 = FilmeBuilder.umFilme().comValor(2.6).agora();
		Filme f2 = new Filme("Tempestade de Areia em Alto Mar", 1, 4.9);
		Filme f3 = new Filme("Historias que Nossas Babás Não Contavam", 1, 5d);
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(f);
		filmes.add(f1);
		filmes.add(f2);
		filmes.add(f3);
		Locacao l1 = service.alugarFilme(u, filmes);
		Assert.assertEquals(7.5, l1.getValor(), 0.001);
	}

	@Test
	public void testeDescontos100PorCento() throws FilmeSemEstoqueException, LocacaoException{
		Filme f = FilmeBuilder.umFilme().comValor(2.5).agora();
		Filme f1 = FilmeBuilder.umFilme().comValor(2.6).agora();
		Filme f2 = new Filme("Tempestade de Areia em Alto Mar", 1, 4.9);
		Filme f3 = new Filme("Historias que Nossas Babás Não Contavam", 1, 5d);
		Filme f4 = new Filme("Historias que Nossas Babás Não Contavam", 1, 5d);
		Filme f5 = new Filme("Historias que Nossas Babás Não Contavam", 1, 2d);
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(f);
		filmes.add(f1);
		filmes.add(f2);
		filmes.add(f3);
		filmes.add(f4);
		filmes.add(f5);
		Locacao l1 = service.alugarFilme(u, filmes);
		Assert.assertEquals(0, l1.getValor(), 0.001);
	}
	
	@Test
	@Ignore //ignora esse test por enquanto
	public void testeNaoDevolverFilmeNoDomingo() throws FilmeSemEstoqueException, LocacaoException{
		List<Filme> filmes = Arrays.asList(new Filme("Filme", 1, 1.5));
		Locacao l = service.alugarFilme(u, filmes);
		boolean ehSegunda = DataUtils.verificarDiaSemana(l.getDataRetorno(), Calendar.MONDAY);
		Assert.assertTrue(ehSegunda);
	}
	
	@Test(expected=LocacaoException.class)
	public void naoDeveAlugarFilmeParaNegativado() throws FilmeSemEstoqueException, LocacaoException{
		Usuario u = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.when(spcService.possuiNegativacao(u)).thenReturn(true);
		
		service.alugarFilme(u, filmes);
	}
}
