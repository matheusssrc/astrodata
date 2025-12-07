package br.com.projeto.modelo;

public class Planeta extends CorpoCeleste {

    private static final long serialVersionUID = 1L;

    private boolean habitavel;

    public Planeta(String nome, double massaKg, long distanciaEmAnosLuz, boolean habitavel) {
        super(nome, massaKg, distanciaEmAnosLuz);
        this.habitavel = habitavel;
    }

    public boolean isHabitavel() {
        return habitavel;
    }

    public void setHabitavel(boolean habitavel) {
        this.habitavel = habitavel;
    }

    @Override
    public String getTipoCorpo() {
        return "Planeta";
    }
}