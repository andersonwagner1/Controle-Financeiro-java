package br.com.controlefinanceiro.model.emurador;

public enum EnumRelatorio {
	RENDA("Credito", "C"),
	RENDIMENTO("Rendimento", "C"),
	CREDITO("Creditos", "C"),
	MENSAL("Mensalidadades", "D"),
	DEBITO("Debito", "D"),
	CARTAO("Cartao", "D"),
	TRANSFERENCIA("Transferencia","T"), 
	APLICACAO("Aplicação", "A"), 
	RESGATE("Resgate", "R"),
	RENDIMENTO_NEGATIVO("Perdas", "R");
	
	
	private String dsNome;
	private String sgNome;
	
	EnumRelatorio(String dsNome, String sgNome){
		this.dsNome = dsNome;
		this.sgNome = sgNome;
	}

	public String getDsNome() {
		return dsNome;
	}

	public void setDsNome(String dsNome) {
		this.dsNome = dsNome;
	}

	public String getSgNome() {
		return sgNome;
	}

	public void setSgNome(String sgNome) {
		this.sgNome = sgNome;
	}
	
	
}
