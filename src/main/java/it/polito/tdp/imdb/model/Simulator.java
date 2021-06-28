package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Simulator {
	
	//input
	int giorni;
	//output
	int pause;
	//Mappa per il giorno e l'attore scelto
	Map<Integer,Actor> attoriIntervistati;
	//model
	Graph<Actor, DefaultWeightedEdge> grafo;
	//una lista da cui poter prendere un elemento casuale
	List<Actor> attoriDisponibili;
	
	public Simulator(int n, Graph<Actor, DefaultWeightedEdge> grafo) {
		this.giorni = n;
		this.grafo = grafo;
	}
	
	public void init() {
		attoriIntervistati = new HashMap<Integer, Actor>();
		this.pause = 0;
		//riempo la lista di tutti i vertici del grafo
		this.attoriDisponibili = new ArrayList<Actor>(this.grafo.vertexSet());
	}
	
	public void run() {
		for(int i = 1; i<this.giorni ; i++) {
			Random rand = new Random();
			
			//il primo giorno oppure il girono dopo essersi preso una pausa (quindi non c'e' nella mappa il giorno precedente)
			if(i == 1 || !attoriIntervistati.containsKey(i-1)) {
				//scelgo casualmente
				Actor actor = attoriDisponibili.get(rand.nextInt(attoriDisponibili.size()));
				attoriIntervistati.put(i, actor);
				//una volta messo nella lista di intevistati lo tolgo da quella dei disponibili
				attoriDisponibili.remove(actor);
				
				System.out.println("Giorni: "+i+" - selezionato autore casualemtne: "+actor.toString()+"\n");
				continue;
			}
			
			
			//il produt ha intervistato per due giorni di fila attori dello stesso genere
			if(i >=3 &&attoriIntervistati.containsKey(i-1) && attoriIntervistati.containsKey(i-2) &&
					 attoriIntervistati.get(i-1).getGender().equals(attoriIntervistati.get(i-2).getGender())) {
				
				//con il 90% fa una pausa
				if(rand.nextFloat() <= 0.9) {
					this.pause ++;
					System.out.println("Giorno: "+i+" pausa");
					continue;
				}
			}
			
			
			//in questo caso il produttore si fa, se possibile, consigliare dell'utimo intervistato
			if(rand.nextFloat() <= 0.6) {
				//in questo caso scelgo ancora casualemente
				Actor actor = attoriDisponibili.get(rand.nextInt(attoriDisponibili.size()));
				attoriIntervistati.put(i, actor);
				attoriDisponibili.remove(actor);
				System.out.println("Giorni: "+i+" - selezionato autore casualemtne: "+actor.toString()+"\n");
				continue;
			}
			else {
				//altrimenti mi faccio consigliare
				Actor ultimo = attoriIntervistati.get(i-1);
				Actor consigliato = this.getAttoreConsigliato(ultimo); 
				
				//se non esiste o non è nella lista dei disponibili
				if(consigliato == null || !attoriDisponibili.contains(consigliato)) {
					Actor actor = attoriDisponibili.get(rand.nextInt(attoriDisponibili.size()));
					attoriIntervistati.put(i, actor);
					attoriDisponibili.remove(actor);
					System.out.println("Giorno: "+i+" - selezionato casualemente "+actor.toString());
					continue;
				}else {
					attoriIntervistati.put(i, consigliato);
					//lo rimuovo
					attoriDisponibili.remove(consigliato);
					System.out.println("Giorno: "+i+" - selezionato autore consigliato "+consigliato.toString());
					continue;
				}
			}
		}
	}

	private Actor getAttoreConsigliato(Actor ultimo) {
		Actor consigliato = null;
		int peso = 0;
		for(Actor a : Graphs.neighborListOf(this.grafo, ultimo)) {
			if(this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, a)) > peso){
				consigliato = a;
			}
		}
		return consigliato;
	}
	
	public int getPause() {
		return this.pause;
	}
	
	public Collection<Actor> getAttoriIntervistati(){
		return this.attoriIntervistati.values();
	}
}
