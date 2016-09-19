/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author brgma_000
 */
public class EstadoAFND {

    private String id;
    private String name;
    private boolean isInitial;
    private boolean isFinal;

    public EstadoAFND() {
    }

    public EstadoAFND(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsInitial() {
        return isInitial;
    }

    public void setIsInitial(boolean isInitial) {
        this.isInitial = isInitial;
    }

    public boolean isIsFinal() {
        return isFinal;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    @Override
    public String toString() {
        String imprimir = "";
        imprimir += "id= " + id + " name= " + name;
        if (isInitial) {
            imprimir += " es estado inicial";
        }
        if (isFinal) {
            imprimir += " es estado final";
        }
        return imprimir;
    }

}
