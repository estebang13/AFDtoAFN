/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.ArrayList;

/**
 *
 * @author brgma_000
 */
public class EstadoAFD {

    private String idStateFrom;
    private String idStateTo;
    private boolean esEstadoInicial;
    private boolean esEstadoFinal;
    private ArrayList<EstadoAFND> states;
    private String letter;

    public EstadoAFD() {
        this.idStateFrom = "";
        this.idStateTo = "";
        this.esEstadoInicial = false;
        this.esEstadoFinal = false;
        this.states = new ArrayList<>();
        this.letter = "";
    }

    public String getIdStateFrom() {
        return idStateFrom;
    }

    public void setIdStateFrom(String idStateFrom) {
        this.idStateFrom = idStateFrom;
    }

    public String getIdStateTo() {
        return idStateTo;
    }

    public void setIdStateTo(String idStateTo) {
        this.idStateTo = idStateTo;
    }

    public boolean isEsEstadoInicial() {
        return esEstadoInicial;
    }

    public void setEsEstadoInicial(boolean esEstadoInicial) {
        this.esEstadoInicial = esEstadoInicial;
    }

    public boolean isEsEstadoFinal() {
        return esEstadoFinal;
    }

    public void setEsEstadoFinal(boolean esEstadoFinal) {
        this.esEstadoFinal = esEstadoFinal;
    }

    public ArrayList<EstadoAFND> getStates() {
        return states;
    }

    public void setStates(ArrayList<EstadoAFND> states) {
        this.states = states;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }
    
   @Override
    public String toString() {
        String imprimir = "";
        imprimir += "From: " + idStateFrom + " To: " + idStateTo + " Letter: "+ letter;
        imprimir += " Inicial: " + esEstadoInicial + " Final: "+ esEstadoFinal;
        return imprimir;
    }

}
