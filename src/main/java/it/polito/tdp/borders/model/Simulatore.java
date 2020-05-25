package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {

	//modello -> stato sistema ad ogni passo 
	private Graph<Country,DefaultEdge> grafo;
	
	//tipi di evento? -> coda prioritaria 
	private PriorityQueue<Evento> queue;
	
	//parametri della simulazione
	private int N_migranti = 1000;
	private Country partenza;
	
	
	//valori in output 
	private int t = -1;
	private Map<Country,Integer> stanziali;
	
	
	public void init(Country partenza,Graph<Country,DefaultEdge> grafo) {
		
		this.grafo = grafo;
		this.partenza = partenza;
		
		//impostazione dello stato iniziale
		
		this.t = 1;
		stanziali = new HashMap<>();
		for(Country c : this.grafo.vertexSet()) {
			stanziali.put(c, 0);
		}
		//creo la coda
		this.queue = new PriorityQueue<>();
		//inserisco il primo evento
		this.queue.add(new Evento(t,partenza,N_migranti));	
	}
	
	public void run() {
		//finchè ho un evento nella coda lo estraggo e lo eseguo;
		Evento e;
		while((e = this.queue.poll()) != null) {
			this.t = e.getT();
			//ESEGUO L'EVENTO E
			int nPersone = e.getN();
			Country stato = e.getStato();
			//cerco i vicini di stato 
			List<Country> vicini = Graphs.neighborListOf(this.grafo, stato);
			
			int migranti = (nPersone / 2) / vicini.size(); //la meta dei migranti che sono arrivati si spostano in parti uguali negli stati vicini
			
			if(migranti > 0 ) {
				for(Country confinante : vicini) {
					queue.add(new Evento(e.getT()+1,confinante,migranti));
				}
			}
			
			int stanziali = nPersone - migranti * vicini.size();
			
			this.stanziali.put(stato, this.stanziali.get(stato) + stanziali);
		}
		
	}
	
	public Map<Country,Integer> getStanziali(){
		return this.stanziali;
	}
	
	public Integer getT() {
		return this.t;
	}
	
}
