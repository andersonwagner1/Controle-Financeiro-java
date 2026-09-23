package br.com.controlefinanceiro.model.emurador;

public enum EnumTipoMovimentacao {
	CREDITO("Credito", "C"), DEBITO("Debito", "D"), TRANSFERENCIA("Transferencia","T"), APLICACAO("Aplicação", "A"), RESGATE("Resgate", "R");
	
	private String dsNome;
	private String sgNome;
	
	EnumTipoMovimentacao(String dsNome, String sgNome){
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
