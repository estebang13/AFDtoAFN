/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author brgma_000
 */
public class Model {

    private ArrayList<EstadoAFND> estadosAFND;
    private ArrayList<String> alfabeto;
    private ArrayList<String>[][] transicionesAFND;
    private EstadoAFND EstadoInicialAFND;
    private ArrayList<EstadoAFND> estadosFinalesAFND;
    private boolean esAFND;
    private boolean tieneEpsilon;
    private static Model model;
    private ArrayList<EstadoAFD> estadosAFD;
    private ArrayList<EstadoAFD> transicionesAFD;
    private ArrayList<EstadoAFD> finalStates;
    private ArrayList<EstadoAFD> noFinalStates;
    private ArrayList<EstadoAFD> auxStates;
    private ArrayList<EstadoAFD> auxStates1;
    private ArrayList<ArrayList<EstadoAFD>> subConjuntos;

    private int contador;

    private Model() {
        this.estadosAFND = new ArrayList<>();
        this.alfabeto = new ArrayList<>();
        this.estadosAFD = new ArrayList<>();
        this.estadosFinalesAFND = new ArrayList<>();
        this.transicionesAFD = new ArrayList<>();
        this.finalStates = new ArrayList<>();
        this.noFinalStates = new ArrayList<>();
        this.subConjuntos = new ArrayList<>();
        this.esAFND = false;
        this.tieneEpsilon = false;
        this.contador = 0;
    }

    public static Model getInstanceOf() {
        return (model == null) ? model = new Model() : model;
    }

    public void ejecutarPrograma() {
        cargarAFND();
        if (esAFND) {
            System.out.println("Es un automata AFND");
            convertirAFNDaAFD();
            this.minimizeAFD();
        } else {
            System.out.println("Es un automata AFD");
            cargarAFD();
        }

        this.minimizeAFD();

//        estadosAFD.stream().forEach((AFDstate) -> {
//            if (AFDstate.isEsEstadoInicial()) {
//                System.out.print("Este estado es inicial\t");
//            }
//            if (AFDstate.isEsEstadoFinal()) {
//                System.out.print("Este estado es final\t");
//            }
//        });
//
//        transicionesAFD.stream().forEach((AFDstate) -> {
//            System.out.println("From: " + AFDstate.getIdStateFrom() + ", To:" + AFDstate.getIdStateTo()
//                    + " with: " + AFDstate.getLetter());
//        });
    }

    public void cargarAFND() {
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File("C:\\Users\\brgma_000\\Desktop\\ejemploAFD.jff");
        try {
            Document document = (Document) builder.build(xmlFile);
            Element rootNode = document.getRootElement();
            List list = rootNode.getChildren("automaton").get(0).getChildren("state");
            list.stream().map((node) -> {
                EstadoAFND state = new EstadoAFND(((Element) node).getAttributeValue("id"),
                        ((Element) node).getAttributeValue("name"));
                state.setIsInitial(((((Element) node).getChild("initial")) != null));
                state.setIsFinal(((((Element) node).getChild("final")) != null));
                return state;
            }).forEach((state) -> {
                estadosAFND.add((EstadoAFND) state);
                if (((EstadoAFND) state).isIsInitial()) {
                    EstadoInicialAFND = (EstadoAFND) state;
                }
                if (((EstadoAFND) state).isIsFinal()) {
                    estadosFinalesAFND.add((EstadoAFND) state);
                }
            });

            transicionesAFND = new ArrayList[estadosAFND.size()][estadosAFND.size()];
            for (int i = 0; i < estadosAFND.size(); i++) {
                for (int j = 0; j < estadosAFND.size(); j++) {
                    transicionesAFND[i][j] = new ArrayList<>();
                }
            }

            list = rootNode.getChildren("automaton").get(0).getChildren("transition");

            for (Object node : list) {
                String letter = ((Element) node).getChildTextNormalize("read");
                String stateFrom = ((Element) node).getChildTextNormalize("from");
                String stateTo = ((Element) node).getChildTextNormalize("to");

                if (letter.equals("")) {
                    transicionesAFND[buscarPosEstado(stateFrom)][buscarPosEstado(stateTo)].add("E");
                    esAFND = true;
                    tieneEpsilon = true;
                } else {
                    transicionesAFND[buscarPosEstado(stateFrom)][buscarPosEstado(stateTo)].add(letter);
                    System.out.println(transicionesAFND[buscarPosEstado(stateFrom)][buscarPosEstado(stateTo)]);
                    if (transicionesAFND[buscarPosEstado(stateFrom)][buscarPosEstado(stateTo)].size() > 1) {
                        esAFND = true;
                    }
                }
                if (!alfabeto.contains(letter)) {
                    alfabeto.add(letter);
                }
            }

            alfabeto.sort(null);

        } catch (JDOMException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void convertirAFNDaAFD() {
        int posInicio = (tieneEpsilon) ? 1 : 0;

        EstadoAFD InitialStateAFD = new EstadoAFD();

        int i;

        if (tieneEpsilon) {
            System.out.println("Es un automata AFND con transicion E");

            i = buscarPosEstado(EstadoInicialAFND.getId());

            for (int j = 0; j < transicionesAFND[i].length; j++) {
                if (transicionesAFND[i][j].contains("E")) {
                    InitialStateAFD.getStates().add(estadosAFND.get(j));
                    buscarLambdaConeccion(InitialStateAFD, j);
                }
            }
        } else {
            InitialStateAFD.getStates().add(EstadoInicialAFND);
        }

        InitialStateAFD.setIdStateFrom("0");
        InitialStateAFD.setIdStateTo("0");
        InitialStateAFD.setEsEstadoInicial(true);

        estadosAFD.add(InitialStateAFD);
        contador++;

        for (i = 0; i < estadosAFD.size(); i++) {
            if (Integer.parseInt(estadosAFD.get(i).getIdStateTo()) < contador) {
                for (int j = posInicio; j < alfabeto.size(); j++) {
                    EstadoAFD tempState = new EstadoAFD();
                    for (EstadoAFND state : estadosAFD.get(i).getStates()) {
                        int posEstado = buscarPosEstado(state.getId());
                        tempState.setIdStateFrom(estadosAFD.get(i).getIdStateTo());
                        for (int l = 0; l < transicionesAFND[posEstado].length; l++) {
                            if (transicionesAFND[posEstado][l].contains(alfabeto.get(j))) {
                                tempState.getStates().add(estadosAFND.get(l));
                            }
                        }
                    }
                    if (!tempState.getStates().isEmpty()) {
                        ArrayList<EstadoAFND> tempCollection = new ArrayList<>();
                        tempCollection.addAll(tempState.getStates());
                        for (int k = 0; k < tempState.getStates().size(); k++) {
                            int m = buscarPosEstado(tempState.getStates().get(k).getId());
                            for (int l = 0; l < transicionesAFND[m].length; l++) {
                                if (transicionesAFND[m][l].contains("E")) {
                                    if (!tempCollection.contains(estadosAFND.get(l))) {
                                        tempCollection.add(estadosAFND.get(l));
                                    }
                                    buscarLambdaConeccionSateAFN(tempCollection, l);
                                }
                            }
                        }

                        String id = buscarEstado(tempCollection);
                        tempCollection.sort(Comparator.comparing(EstadoAFND::getId));
                        tempState.setStates(tempCollection);
                        tempState.setEsEstadoFinal(esEstadoFinal(tempState));
                        if (id.equals("")) {
                            id = String.valueOf(contador);
                            contador++;
                            tempState.setIdStateTo(id);
                            estadosAFD.add(tempState);
                        }
                        tempState.setIdStateTo(id);
                        tempState.setLetter(alfabeto.get(j));
                        transicionesAFD.add(tempState);

                    }
                }
            }
        }

    }

    public void buscarLambdaConeccion(EstadoAFD stateInit, int posBusqueda) {
        for (int j = 0; j < transicionesAFND[posBusqueda].length; j++) {
            if (transicionesAFND[posBusqueda][j].contains("E")) {
                stateInit.getStates().add(estadosAFND.get(j));
                buscarLambdaConeccion(stateInit, j);
            }
        }
    }

    public void buscarLambdaConeccionSateAFN(ArrayList<EstadoAFND> tempCollection, int posBusqueda) {
        for (int j = 0; j < transicionesAFND[posBusqueda].length; j++) {
            if (transicionesAFND[posBusqueda][j].contains("E")) {
                if (!tempCollection.contains(estadosAFND.get(j))) {
                    tempCollection.add(estadosAFND.get(j));
                }
                buscarLambdaConeccionSateAFN(tempCollection, j);
            }
        }
    }

    public void buscarLetraConeccion(EstadoAFD state, int posBusqueda, String letra) {
        for (int j = 0; j < transicionesAFND[posBusqueda].length; j++) {
            if (transicionesAFND[posBusqueda][j].contains(letra)) {
                state.getStates().add(estadosAFND.get(j));
            }
        }
    }

    public int buscarPosEstado(String idEstado) {
        int pos = 0;
        for (int i = 0; i < estadosAFND.size(); i++) {
            if (idEstado.equals(estadosAFND.get(i).getId())) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    public String buscarEstado(ArrayList<EstadoAFND> groupStates) {
        String id = "";
        for (EstadoAFD AFNstate : estadosAFD) {
            if (AFNstate.getStates().containsAll(groupStates)) {
                id = AFNstate.getIdStateTo();
                break;
            }
        }
        return id;
    }

    public boolean esEstadoFinal(EstadoAFD estado) {
        boolean esFinal = false;
        for (EstadoAFND estadosFinal : estadosFinalesAFND) {
            if (estado.getStates().contains(estadosFinal)) {
                esFinal = true;
                break;
            }
        }
        return esFinal;
    }

    public void cargarAFD() {
        estadosAFND.stream().map((estadosAFND1) -> {
            EstadoAFD estado = new EstadoAFD();
            estado.setEsEstadoFinal(estadosAFND1.isIsFinal());
            estado.setEsEstadoInicial(estadosAFND1.isIsInitial());
            estado.setIdStateFrom(estadosAFND1.getId());
            estado.setIdStateTo(estadosAFND1.getId());
            return estado;
        }).forEach((estado) -> {
            estadosAFD.add(estado);
        });

        for (int i = 0; i < estadosAFND.size(); i++) {
            for (int j = 0; j < estadosAFND.size(); j++) {
                if (!transicionesAFND[i][j].isEmpty()) {
                    EstadoAFD estado = new EstadoAFD();
                    estado.setEsEstadoFinal(estadosAFND.get(i).isIsFinal());
                    estado.setEsEstadoInicial(estadosAFND.get(i).isIsInitial());
                    estado.setIdStateFrom(estadosAFND.get(i).getId());
                    estado.setIdStateTo(estadosAFND.get(j).getId());
                    estado.setLetter(transicionesAFND[i][j].get(0));
                    transicionesAFD.add(estado);
                }
            }
        }
    }

    public void minimizeAFD() {
        this.divideAFD();
        this.transformSubConjuntos();
        this.transformSubConjuntos();
        System.out.println(subConjuntos);

//        subConjuntos.stream().forEach((AFDstate) -> {
//            System.out.println("SubConjunto " + AFDstate);
//        });
    }

    // Divide el AFD en los estados finales y no finales
    public void divideAFD() {
        estadosAFD.stream().forEach((AFDstate) -> {
            if (AFDstate.isEsEstadoFinal()) {
                finalStates.add(AFDstate);
            } else {
                noFinalStates.add(AFDstate);
            }
        });
        subConjuntos.add(noFinalStates);
        subConjuntos.add(finalStates);
    }

    //Este metodo recorre todos los subconjuntos y los divide segun sea necesario para despues armar los nuevos estados
    public int transformSubConjuntos() {
        for (int i = 0; i < alfabeto.size(); i++) {
            for (int j = 0; j < subConjuntos.size(); j++) {
                ArrayList<EstadoAFD> auxiliar = new ArrayList<>();
                if (subConjuntos.get(j).size() > 1) {
                    for (int k = 0; k < subConjuntos.get(j).size(); k++) {
                        for (int l = 0; l < transicionesAFD.size(); l++) {
                            if (subConjuntos.get(j).get(k).getIdStateFrom().equals(transicionesAFD.get(l).getIdStateFrom())) {
                                if (transicionesAFD.get(l).getLetter().equals(alfabeto.get(i))) {
                                    EstadoAFD estado = getEstadoId(transicionesAFD.get(l).getIdStateTo());
                                    if (!subConjuntos.get(j).contains(estado)) {
                                        auxiliar.add(subConjuntos.get(j).get(k));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (!auxiliar.isEmpty()) {
                    ArrayList<EstadoAFD> auxiliar1 = new ArrayList<>();
                    auxiliar1.addAll(auxiliar);
                    for (EstadoAFD auxiliar2 : auxiliar1) {
                        subConjuntos.get(j).remove(auxiliar2);
                    }
                    auxiliar.clear();
                    subConjuntos.add(auxiliar1);
                    return 0;
                }
            }
        }
        return 0;
    }

    public EstadoAFD getEstadoId(String idEstado) {
        for (int i = 0; i < estadosAFD.size(); i++) {
            if (idEstado.equals(estadosAFD.get(i).getIdStateFrom())) {
                return estadosAFD.get(i);
            }
        }
        return null;
    }

    // Verifica si el estado y su transicion para ver que no se encuentre en otro subconjunto
    public boolean isInOtherSubconjunto(EstadoAFD estado, ArrayList<EstadoAFD> currentList) {
        boolean isInOther = false;
        for (int i = 0; i < subConjuntos.size(); i++) {
            this.auxStates = new ArrayList<>();
            if (subConjuntos.get(i).size() > 1) {
                for (int j = 0; j < subConjuntos.get(i).size(); j++) {
                    if (estado.getIdStateTo().equals(subConjuntos.get(i).get(j).getIdStateFrom())) {
                        isInOther = true;
                        auxStates.add(subConjuntos.get(i).get(j));
                    }
                }
            }
        }
        return isInOther;
    }
}
