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
		return doc;
	}

	public static String getPageTitle(String url) {
		Document doc = getDocument(url);
		return doc.title();
	}

	public static void main(String[] args) {
		System.out.println("Please enter you search terms below: \n");
		Scanner scanner = new Scanner(System.in);
		String searchQuery = scanner.nextLine();
		List<String> results = new ArrayList<String>();
		results.add("https://en.wikipedia.org/wiki/Java_(programming_language)");
		System.out.println("About " + results.size() + " results");
		for (String result : results) {
			System.out.println(getPageTitle(result));
			System.out.println(result);
		}
	}
}