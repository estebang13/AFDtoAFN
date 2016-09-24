/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto1paradigmas;

import java.util.Scanner;

/**
 *
 * @author Bryan Gonzalez Marchena y Esteban Guerrero Gutierrez
 */
public class Proyecto1Paradigmas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println ("Empezamos el programa");
        System.out.println ("Por favor introduzca la direccion del archivo:");
        String entradaTeclado = "";
        Scanner entradaEscaner = new Scanner (System.in); //Creación de un objeto Scanner
        entradaTeclado = entradaEscaner.nextLine (); //Invocamos un método sobre un objeto Scanner
        Model.Model.getInstanceOf().ejecutarPrograma(entradaTeclado);
    }

}
