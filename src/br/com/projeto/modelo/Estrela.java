package br.com.projeto.modelo;

public class Estrela extends CorpoCeleste {

    private static final long serialVersionUID = 1L;

    private String tipoEspectral;

    public Estrela(String nome, double massaKg, long distanciaEmAnosLuz, String tipoEspectral) {
        super(nome, massaKg, distanciaEmAnosLuz);
        this.tipoEspectral = tipoEspectral;
    }

    public String getTipoEspectral() {
        return tipoEspectral;
    }

    public void setTipoEspectral(String tipoEspectral) {
        this.tipoEspectral = tipoEspectral;
    }

    @Override
    public String getTipoCorpo() {
        return "Estrela";
    }
}