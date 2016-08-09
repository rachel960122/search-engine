package com.flatironschool.javacs;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;


/**
 * Represents the results of a search query.
 *
 */
public class WikiSearch {
	
	private Map<String, Double> map;
	private JedisIndex index;

	/**
	 * Constructor.
	 * 
	 * @param map
	 */
	public WikiSearch() throws IOException{
		Jedis jedis = JedisMaker.make();
		this.index = new JedisIndex(jedis); 
		this.map = new HashMap<String, Double>();
	}
	
	/**
	 * Prints the contents in order of term frequency.
	 * 
	 * @param map
	 */
	private void print(List<String> list) {
		for (String elem: list) {
			System.out.println(elem);
		}
	}
	
	/**
	 * Computes the relevance of a search with multiple terms.
	 * 
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */
	/* protected double totalRelevance(Double rel1, Double rel2) {
		// simple starting place: relevance is the sum of the term frequencies.
		return rel1 + rel2;
	} */

	public List<String> search(Map<String, List<String>> wordMap){
		Map<String, Double> map = new HashMap<String, Double>();

		List<String> compulsory = wordMap.get("a");
		List<String> minus = wordMap.get("m");
		List<String> optional = wordMap.get("o");
		List<String> sites = wordMap.get("s");

		//Map<String, Double> res = new HashMap<String, Double>();
		
		Set<String> urls = index.termCounterKeys();
		Set<String> newUrls = new HashSet<String>();

		for (String url: urls){
			url = url.substring(12);
			newUrls.add(url);
			//map.put(url, 0.0);
		}

		for (String word: compulsory){
			Set<String> urlSet = searchTerm(word);
			newUrls.retainAll(urlSet);
		}

		for (String word: minus){
			Set<String> urlSet = searchTerm(word);
			newUrls.removeAll(urlSet);
		}

		if (sites != null) {
			newUrls.retainAll(sites);
		}

		for (String url: newUrls){
			if (compulsory != null){
				for (String term: compulsory){
					if (!map.containsKey(url)){
						map.put(url, index.tfidf(term, url));
					} else {
						map.put(url, map.get(url) + index.tfidf(term, url));
					}
				}
			}
			if (optional != null){
				for (String term: optional){
					if (!map.containsKey(url)){
						map.put(url, index.tfidf(term, url));
					} else {
						map.put(url, map.get(url) + index.tfidf(term, url));
					}
				}
			}
		}

		List<Entry<String, Double>> sortedEntries = sort(map);
		List<String> res = new LinkedList<String>();
		for (Entry<String, Double> entry: sortedEntries){
			res.add(entry.getKey());
		}
		return res;
	}

	/**
	 * Sort the results by relevance.
	 * 
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, Double>> sort(Map<String, Double> map) {
		List<Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(map.entrySet());
		Comparator<Entry<String, Double>> comparator = new Comparator<Entry<String, Double>>(){
			public int compare(Entry<String, Double> one, Entry<String, Double> two){
				return (two.getValue()).compareTo(one.getValue());
			}
		};
		Collections.sort(list, comparator);
        return list;
	}

	/**
	 * Performs a search and makes a WikiSearch object.
	 * 
	 * @param term
	 * @param index
	 * @return
	 */
	public Set<String> searchTerm(String term) {
		Map<String, Double> map = index.getRelevance(term);
		return map.keySet();
	}

	public static void main(String[] args) throws IOException {
		// search for the first term
		Map<String, List<String>> test = new HashMap<String, List<String>>();
		test.put("a", new ArrayList<String>());
		test.get("a").add("python");
		test.get("a").add("programming");
		test.put("m", new ArrayList<String>());
		test.get("m").add("electronic");

		WikiSearch searcher = new WikiSearch();
		List<String> result = searcher.search(test);
		for (String elem: result){
			System.out.println(elem);
		}
	}
}
