package br.com.controlefinanceiro.model.emurador;

public enum EnumBancoImportacao {
	
	CORRENTE_BRADESCO_PRIME("Bradesco - corrente - PRIME",0l ),
	CORRENTE_BRADESCO("Bradesco - corrente",0l ),
	CARTAO_NUBANK("Nubank - Cartão Credito", 26L), 
	CORRENTE_NUBANK("Nubank - Corrente",16l), 
	TESOURO_23("Tesouro Direito - Prefixado", 23L),
	TESOURO_24("Tesouro Direito - Selic", 24L),
	TESOURO_25("Tesouro Direito - IPCA", 25L);
	
	private String dsNome;
	private Long bancoConta;
	
	EnumBancoImportacao(String dsNome, Long bancoConta){
		this.dsNome = dsNome;
		this.bancoConta  = bancoConta;
	}

	public String getDsNome() {
		return dsNome;
	}

	public void setDsNome(String dsNome) {
		this.dsNome = dsNome;
	}



	public Long getBancoConta() {
		return bancoConta;
	}

	public void setBancoConta(Long bancoConta) {
		this.bancoConta = bancoConta;
	}
	
	
	
}
