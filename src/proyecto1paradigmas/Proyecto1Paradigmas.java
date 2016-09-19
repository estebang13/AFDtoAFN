/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto1paradigmas;

/**
 *
 * @author brgma_000
 */
public class Proyecto1Paradigmas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Model.Model.getInstanceOf().cargarAFND();
        Model.Model.getInstanceOf().convertirAFNDaAFD();
        Model.Model.getInstanceOf().minimizeAFD();
    }

}
