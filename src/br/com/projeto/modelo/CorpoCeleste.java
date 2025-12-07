package br.com.projeto.modelo;

import java.io.Serializable;

public abstract class CorpoCeleste implements Serializable {
    
    // Identificador de versão para serialização (Obrigatório em classes Serializable)
    private static final long serialVersionUID = 1L;

    private int id; // ID para persistência no banco
    private String nome;
    private double massaKg; 
    private long distanciaEmAnosLuz;

    public CorpoCeleste(String nome, double massaKg, long distanciaEmAnosLuz) {
        this.nome = nome;
        this.massaKg = massaKg;
        this.distanciaEmAnosLuz = distanciaEmAnosLuz;
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    // Sem setNome para manter a imutabilidade do nome após a criação (Segurança)

    public double getMassaKg() {
        return massaKg;
    }

    public long getDistanciaEmAnosLuz() {
        return distanciaEmAnosLuz;
    }

    // --- Métodos Abstratos e Sobrescritas ---

    // Método abstrato para garantir polimorfismo
    public abstract String getTipoCorpo();

    @Override
    public String toString() {
        return getTipoCorpo() + ": " + nome + 
               " (Massa: " + String.format("%.2e", massaKg) + " Kg)";
    }
}