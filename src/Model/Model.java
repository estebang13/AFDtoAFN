/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author Bryan Gonzalez Marchena y Esteban Guerrero Gutierrez
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
    String urlFile;
    SAXBuilder builder;
    File xmlFile;

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

    public void ejecutarPrograma(String url) {
        cargarAFND(url);
        urlFile = url;
        if (esAFND) {
            System.out.println("Es un automata AFND");
            convertirAFNDaAFD();
        } else {
            System.out.println("Es un automata AFD");
            cargarAFD();
        }

        System.out.println("Estados:");
        System.out.println(estadosAFD);
        System.out.println("Transiciones:");
        System.out.println(transicionesAFD);

        this.minimizeAFD();

        System.out.println(subConjuntos);

        createXML();
    }

    //Este metodo genera la carga de un automata deterministico no deterministico
    public void cargarAFND(String url) {
        builder = new SAXBuilder();
        xmlFile = new File(url);
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

    //Metodo principal encargado de convertir un automata finito no deterministico en un automata finito deterministico
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
                            EstadoAFD tempState1 = new EstadoAFD();
                            tempState1.setIdStateFrom(id);
                            tempState1.setIdStateTo(id);
                            tempState1.setEsEstadoFinal(tempState.isEsEstadoFinal());
                            tempState1.getStates().addAll(tempCollection);
                            estadosAFD.add(tempState1);
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

    // Metodo encargado de buscar por medio del id del estado
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

    //Metodo que busca un estado entre los estados de automatas deterministicos
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

    //Este metodo corrobora si el estado es final o no
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

    // Metodo encargado de hacer la carga del automata finito deterministico
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

    //Metodo principal encargado de minimizar el automata
    public void minimizeAFD() {
        this.divideAFD();
        this.transformSubConjuntos();
        this.createXML();
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
    public void transformSubConjuntos() {
        boolean bandera = false;
        int aux = (tieneEpsilon) ? 1 : 0;
        for (int i = aux; i < alfabeto.size(); i++) {
            for (int j = 0; j < subConjuntos.size(); j++) {
                ArrayList<EstadoAFD> auxiliar = new ArrayList<>();
                EstadoAFD estadoToComun = new EstadoAFD();
                estadoToComun.setIdStateFrom("comun");
                if (subConjuntos.get(j).size() > 1) {
                    for (int k = 0; k < subConjuntos.get(j).size(); k++) {
                        for (EstadoAFD transicionesAFD1 : transicionesAFD) {
                            if (subConjuntos.get(j).get(k).getIdStateFrom().equals(transicionesAFD1.getIdStateFrom())) {
                                if (transicionesAFD1.getLetter().equals(alfabeto.get(i))) {
                                    EstadoAFD estado = getEstadoId(transicionesAFD1.getIdStateTo());
                                    if (estadoToComun.getIdStateFrom().equals("comun") && estado != null) {
                                        estadoToComun = estado;
                                    }
                                    if (estado != null && !subConjuntos.get(j).contains(estado)) {
                                        if (estado != estadoToComun) {
                                            auxiliar.add(subConjuntos.get(j).get(k));
                                            break;
                                        }
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
                    bandera = true;
                    break;
                }
            }
            if (bandera) {
                bandera = false;
                aux = (tieneEpsilon) ? 1 : 0;
                aux--;
                i = aux;
            }
        }
    }

    // Este metodo obtiene en id de un estado en especifico
    public EstadoAFD getEstadoId(String idEstado) {
        for (EstadoAFD estadosAFD1 : estadosAFD) {
            if (idEstado.equals(estadosAFD1.getIdStateFrom())) {
                return estadosAFD1;
            }
        }
        return null;
    }

    //Este metodo se encargar de convertir el Array en el XML para ser leido por JFLAP
    public void createXML() {
        try {
            addSubConjuntosToFirstState();
            Element structure = new Element("structure");
            Document doc = new Document(structure);
            structure.addContent(new Element("type").setText("fa"));
            Element automaton = new Element("automaton");
            for (int i = 0; i < subConjuntos.size(); i++) {
                Element state = new Element("state");
                state.setAttribute(new Attribute("id", subConjuntos.get(i).get(0).getIdStateFrom()));
                state.setAttribute(new Attribute("name", "q" + subConjuntos.get(i).get(0).getIdStateFrom()));
                for (int j = 0; j < transicionesAFD.size(); j++) {
                    if (subConjuntos.get(i).get(0).getIdStateFrom() == transicionesAFD.get(j).getIdStateFrom()) {
                        if (transicionesAFD.get(j).isEsEstadoInicial() || subConjuntos.get(i).get(0).isEsEstadoInicial()) {
                            state.addContent(new Element("initial"));
                        }
                        if (transicionesAFD.get(j).isEsEstadoFinal()) {
                            state.addContent(new Element("final"));
                        }
                        if (!isNotInSubconjuntos(transicionesAFD.get(j).getIdStateTo()).isEmpty()) {
                            transicionesAFD.get(j).setIdStateTo(isNotInSubconjuntos(transicionesAFD.get(j).getIdStateTo()));
                        }
                        Element transition = new Element("transition");
                        transition.addContent(new Element("from").setText(transicionesAFD.get(j).getIdStateFrom()));
                        transition.addContent(new Element("to").setText(transicionesAFD.get(j).getIdStateTo()));
                        transition.addContent(new Element("read").setText(transicionesAFD.get(j).getLetter()));
                        automaton.addContent(transition);
                    }
                }
                automaton.addContent(state);
            }
            doc.getRootElement().addContent(automaton);
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            String[] parts = urlFile.split(Pattern.quote("."));
            String a = parts[0];
            xmlOutput.output(doc, new FileWriter(a + "Transform.jff"));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Este metodo corrobora la existencia de las transiciones.
    public String isNotInSubconjuntos(String to) {
        boolean isNotInSubconjuntos = false;
        String newTo = "";
        for (int i = 0; i < subConjuntos.size(); i++) {
            if (subConjuntos.get(i).get(0).getFinalStates().contains(to)) {
                isNotInSubconjuntos = true;
                newTo = subConjuntos.get(i).get(0).getIdStateFrom();
                break;
            }
        }
        return newTo;
    }
    
    //Este metodo se encarga de acomodar los SubConjuntos.
    public void addSubConjuntosToFirstState(){
        for (int i = 0; i < subConjuntos.size(); i++) {
            if (subConjuntos.get(i).size() > 0) {
                for(int j=0; j < subConjuntos.get(i).size() ; j++){
                    if(!subConjuntos.get(i).get(0).getFinalStates().contains(subConjuntos.get(i).get(j).getIdStateFrom())){
                    subConjuntos.get(i).get(0).addState(subConjuntos.get(i).get(j).getIdStateFrom());
                    }
                }
            }
        }
    }
    
}
