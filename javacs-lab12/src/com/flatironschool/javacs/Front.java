package com.flatironschool.javacs;


import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Test;

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

public class Front {

	public static Document getDocument(String url) {
		Connection conn = Jsoup.connect(url);
		Document doc;
		try {
			doc = conn.get();
		} catch (IOException e) {
			System.out.println("Page content not available");
			return null;
		}
		Element content = doc.getElementById("mw-content-text");
		Elements paras = content.select("p");
		String path = "/Users/rachelxu/Documents/search-engine/javacs-lab12/src/resources/en.wikipedia.org/files/";
		String title = doc.title();
		try {
			File file = new File(path + processTitle(title));
			if (!file.exists()) {
				file.createNewFile();
			}
			System.out.println("done");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(getPageContent(paras));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return doc;
	}

	public static String processTitle(String title) {
		String entryName = title.split("-")[0].trim();
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

	public static String getPageTitle(String url) {
		Document doc = getDocument(url);
		return doc.title();
	}

	public static String getPageContent(Elements paras) {
		StringBuilder sb = new StringBuilder();
		for (Element para : paras) {
			sb.append(para.text());
		}
		return sb.toString();
	}

	public static String getFileContent(String url) {
		BufferedReader br = null;
		String[] parts = url.split("/");
		String title = parts[parts.length - 1];
		StringBuilder sb = new StringBuilder();
		String path = "/Users/rachelxu/Documents/search-engine/javacs-lab12/src/resources/en.wikipedia.org/files/";
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
		return sb.toString().substring(0, 150);
	}

	public static void main(String[] args) {
		System.out.println(Front.getFileContent("https://en.wikipedia.org/wiki/Java_(programming_language)"));
		// System.out.println("Please enter you search terms below: \n");
		// Scanner scanner = new Scanner(System.in);
		// String searchQuery = scanner.nextLine();
		// List<String> results = new ArrayList<String>();
		// results.add("https://en.wikipedia.org/wiki/Java_(programming_language)");
		// System.out.println("About " + results.size() + " results");
		// for (String result : results) {
		// 	System.out.println(getPageTitle(result));
		// 	System.out.println(result);
		// 	System.out.println(getFirstParagraph(result));
		// }
	}
}