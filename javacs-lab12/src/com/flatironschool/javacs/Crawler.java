package com.flatironschool.javacs;

import java.io.IOException;
import java.util.Queue;
import java.util.LinkedList;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Node;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

public class Crawler {
	private final String source;
	private JedisIndex index;
	private Queue<String> queue = new LinkedList<String>();
	final static WikiFetcher fetcher = new WikiFetcher();
	private int max = 500;
	private volatile int count = 0;

	public Crawler(String source, JedisIndex index) {
		this.source = source;
		this.index = index;
		queue.offer(source);
	}

	public int queueSize() {
		return queue.size();
	}

	public synchronized String crawl() throws IOException {
		if (queue.isEmpty()) {
			return null;
		}

		String url = null;
		while (count < max) {
			// take a url of the list of seed urls
			if (queue.isEmpty()) {
				queue.offer(index.dequeueSeedUrl());
			}
			url =  queue.poll();
			if (index.isIndexed(url)) {
				System.out.println("Already indexed " + url);
			} else {
				System.out.println("Crawling " + url);
				Elements paragraphs = fetcher.fetchWikipedia(url);
				index.indexPage(url, paragraphs);
				queueInternalLinks(paragraphs);
				count++;
				// save the next url to the list of seed urls
				if (count == max) {
					index.enqueueSeedUrl(queue.peek());
				}
			}
		}
		return url;
	}

	public void queueInternalLinks(Elements paragraphs) {
		for (Element paragraph : paragraphs) {
			queueInternalLinks(paragraph);
		}
	}

	public void queueInternalLinks(Element paragraph) {
		Elements elts = paragraph.select("a[href]");
		for (Element elt: elts) {
			String relURL = elt.attr("href");
			
			if (isInternalLink(relURL)) {
				String absURL = elt.attr("abs:href");
				queue.offer(absURL);
			}
		}
	}

	boolean isInternalLink(String href) {
		return href.startsWith("/wiki/");
	}

	public static void main(String[] args) throws IOException {
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis);
		index.addSeedUrls();
		String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		final Crawler crawler = new Crawler(source, index);

		Thread[] t_array = new Thread[8];

		for (int i = 0; i < t_array.length; i++) {
			t_array[i] = new Thread(new Crawl(crawler));
		}

		long startTime = System.currentTimeMillis();
		try {
			for (Thread t : t_array) {
				t.start();
			}
		} catch (JedisDataException e) {
			e.printStackTrace();
		}

		try {
			for (Thread t : t_array) {
				t.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Crawled " + index.termCounterKeys().size() + " pages");
		System.out.println(index.termSet().size() + " terms");
		System.out.println("Took " + (endTime - startTime) + " milliseconds");
	}
}

class Crawl implements Runnable {
	private Crawler crawler;

	public Crawl(Crawler crawler) {
		this.crawler = crawler;
	}

	@Override
	public void run() {
		try {
			crawler.crawl();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}