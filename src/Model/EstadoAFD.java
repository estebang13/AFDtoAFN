/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.ArrayList;

/**
 *
 * @author Bryan Gonzalez Marchena y Esteban Guerrero Gutierrez
 */
public class EstadoAFD {

    private String idStateFrom;
    private String idStateTo;
    private boolean esEstadoInicial;
    private boolean esEstadoFinal;
    private ArrayList<EstadoAFND> states;
    private ArrayList<String> finalStates;
    private String letter;

    public EstadoAFD() {
        this.idStateFrom = "";
        this.idStateTo = "";
        this.esEstadoInicial = false;
        this.esEstadoFinal = false;
        this.states = new ArrayList<>();
        this.finalStates = new ArrayList<>();
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

    public void setFinalStates(ArrayList<String> finalStates) {
        this.finalStates = finalStates;
    }

    public ArrayList<String> getFinalStates() {
        return finalStates;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }
    
    public void addState(String a){
        finalStates.add(a);
    }
    
   @Override
    public String toString() {
        String imprimir = "";
        imprimir += "From: " + idStateFrom + " To: " + idStateTo + " Letter: "+ letter;
        imprimir += " Inicial: " + esEstadoInicial + " Final: "+ esEstadoFinal;
        return imprimir;
    }

}
