package com.flatironschool.javacs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;

public class WikiFetcher {
	private long lastRequestTime = -1;
	private long minInterval = 1000;

	/**
	 * Fetches and parses a URL string, returning a list of paragraph elements.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Elements fetchWikipedia(String url) throws IOException {
		sleepIfNeeded();

		// download and parse the document
		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();

		// select the content text and pull out the paragraphs.
		Element content = doc.getElementById("mw-content-text");

		// TODO: avoid selecting paragraphs from sidebars and boxouts
		Elements paras = content.select("p");

		storePage(doc, paras);

		return paras;
	}

	public void storePage(Document doc, Elements paras) {
		String path = "/Users/rachelxu/Documents/search-engine/javacs-lab12/src/resources/en.wikipedia.org/files/";
		String title = doc.title();
		try {
			File file = new File(path + spaceToUnderscore(title));
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(getPageContent(paras));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getPageContent(Elements paras) {
		StringBuilder sb = new StringBuilder();
		for (Element para : paras) {
			sb.append(para.text());
		}
		return sb.toString();
	}

	public static String[] getFileContent(String url) {
		String[] res = new String[2];
		String path = "/Users/rachelxu/Documents/search-engine/javacs-lab12/src/resources/en.wikipedia.org/files/";
		BufferedReader br = null;
		String[] parts = url.split("/");
		String title = parts[parts.length - 1];
		StringBuilder sb = new StringBuilder();
		try {
			String line;
			br = new BufferedReader(new FileReader(path + title));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		res[0] = underscoreToSpace(title);
		if (sb.length() >= 200) {
			sb.insert(50, "\n");
			sb.insert(100, "\n");
			sb.insert(150, "\n");
			res[1] = sb.toString().substring(0, 200) + " ...";
		} else {
			res[1] = sb.toString();
		}
		return res;
	}

	public static String spaceToUnderscore(String title) {
		int index = title.lastIndexOf("-");
		String entryName = title.substring(0, index - 1);
		StringBuilder sb = new StringBuilder();
		for (char c : entryName.toCharArray()) {
			if (c != ' ') {
				sb.append(String.valueOf(c));
			} else {
				sb.append("_");
			}
		}
		return sb.toString();
	}

	public static String underscoreToSpace(String filename) {
		StringBuilder sb = new StringBuilder();	
		String postfix = " - Wikipedia, the free encyclopedia";
		for (char c : filename.toCharArray()) {
			if (c != '_') {
				sb.append(c);
			} else {
				sb.append(" ");
			}
		}
		sb.append(postfix);
		return sb.toString();
	}

	/**
	 * Reads the contents of a Wikipedia page from src/resources.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Elements readWikipedia(String url) throws IOException {
		URL realURL = new URL(url);

		// assemble the file name
		String slash = File.separator;
		String filename = "resources" + slash + realURL.getHost() + realURL.getPath();

		// read the file
		InputStream stream = WikiFetcher.class.getClassLoader().getResourceAsStream(filename);
		Document doc = Jsoup.parse(stream, "UTF-8", filename);

		// TODO: factor out the following repeated code
		Element content = doc.getElementById("mw-content-text");
		Elements paras = content.select("p");
		return paras;
	}

	/**
	 * Rate limits by waiting at least the minimum interval between requests.
	 */
	private void sleepIfNeeded() {
		if (lastRequestTime != -1) {
			long currentTime = System.currentTimeMillis();
			long nextRequestTime = lastRequestTime + minInterval;
			if (currentTime < nextRequestTime) {
				try {
					//System.out.println("Sleeping until " + nextRequestTime);
					Thread.sleep(nextRequestTime - currentTime);
				} catch (InterruptedException e) {
					System.err.println("Warning: sleep interrupted in fetchWikipedia.");
				}
			}
		}
		lastRequestTime = System.currentTimeMillis();
	}
}
