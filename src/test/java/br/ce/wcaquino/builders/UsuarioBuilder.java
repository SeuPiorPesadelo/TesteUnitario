package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Usuario;

public class UsuarioBuilder {
	
	private Usuario usuario;
	
	private UsuarioBuilder(){
	}
	
	public static UsuarioBuilder umUsuario(){
		UsuarioBuilder ub = new UsuarioBuilder();
		ub.usuario = new Usuario();
		ub.usuario.setNome("Usuario Qualquer");
		return ub;
	}
	
	public Usuario agora(){
		return usuario;
	}
}
