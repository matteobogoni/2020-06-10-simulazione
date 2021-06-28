package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	ImdbDAO dao;
	private Graph<Actor,DefaultWeightedEdge> grafo;
	private Map<Integer,Actor> idMap;
	Simulator sim;
	
	public Model() {
		dao = new ImdbDAO();
		this.idMap = new HashMap<Integer,Actor>();
		this.dao.listAllActors(idMap);
	}
	
	public List<String> getGenre(){
		return dao.listAllGenre();
	}
	
	public void creaGrafo(String g) {
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungo i vertici
		Graphs.addAllVertices(this.grafo, dao.listActorsByGenre(g));
		
		//aggiungo gli archi
		for(Adiacenza a : this.dao.getAdiacenze(g, idMap)) {
			if(this.grafo.containsVertex(a.getA1()) && this.grafo.containsVertex(a.getA2())) {
				Graphs.addEdgeWithVertices(this.grafo, a.getA1(), a.getA2(), a.getPeso());
			}
		}
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Actor> getVerticiGrafo(String g){
		return this.dao.listActorsByGenre(g);
	}
	
	public List<Actor> getNearActors(Actor a){
	//con questo metodo trovo tutti i vertici raggiungibili a partire da uno dato
		ConnectivityInspector<Actor, DefaultWeightedEdge> c1 = new ConnectivityInspector<Actor,DefaultWeightedEdge>(grafo);
		List<Actor> actors = new ArrayList(c1.connectedSetOf(a));
		//tolgo il nodo di partenza dalla lista e cosi ho tutti i vertici raggiungibili
		actors.remove(a);
		//li ordino in ordine di cognome
		Collections.sort(actors);
		
		return actors;
	}
	
	public void simulate(int n) {
		sim = new Simulator(n, this.grafo);
		sim.init();
		sim.run();
	}
	
	public Collection<Actor> getAttoriIntervistati(){
		
		if(sim == null){
			return null;
		}
		
		return sim.getAttoriIntervistati();
	}
	
	public Integer getPause() {
		if(sim == null) {
			return null;
		}
		return sim.getPause();
	}
}
