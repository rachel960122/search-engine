package com.flatironschool.javacs;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

public class QueryParser {
	private Set<String> stoplist;
	OptionParser op;
	Map<String, List<String>> map;

	public QueryParser() {
		stoplist = new HashSet<String>();
		createStoplist();
		op = new OptionParser();
		map = new HashMap<String, List<String>>();
	}

	public void createStoplist() {
		String slash = File.separator;
		String filename = "resources" + slash + "stoplist.txt";
		URL fileURL = QueryParser.class.getClassLoader().getResource(filename);
		String filepath;

		try {
			filepath = URLDecoder.decode(fileURL.getFile(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filepath));
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + filename);
			return;
		}

		while (true) {
			String line;
			try {
				line =  br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				line = null;
			}
			if (line == null) break;
			stoplist.add(line.trim());
		}
	}

	public Map<String, List<String>> processArgument(String argument) {
		String[] words = argument.trim().split(" ");
		List<String> arguments = new ArrayList<String>();
		for (String word : words) {
			if (!isStopWord(word)) {
				arguments.add(word);
			}
		}
		processArguments(arguments);
		return map;
	}

	public List<String> getValues(char option) {
		switch (option) {
			case 'a': return map.get("a");
			case 'm': return map.get("m");
			case 's': return map.get("s");
			case 'o': return map.get("o");
			default: return new ArrayList<String>();
		}
	}

	public void processArguments(List<String> arguments) {
		String[] options = {"a", "m", "s"};
		String[] args = new String[arguments.size()];
		for (int i = 0; i < arguments.size(); i++) {
			args[i] = arguments.get(i);
		}
		List<String> optionalWords = new ArrayList<String>();

		OptionParser parser = new OptionParser();
    OptionSpec<String> aCount = parser.accepts( "a" ).withRequiredArg().ofType( String.class );
    OptionSpec<String> mCount = parser.accepts( "m" ).withRequiredArg().ofType( String.class );
    OptionSpec<String> sCount = parser.accepts( "s" ).withRequiredArg().ofType( String.class );
    for (int i = 0; i < args.length; i++) {
    	if ((i == 0 || (i > 0 && args[i - 1].charAt(0) != '-')) && args[i].charAt(0) != '-') {
    		optionalWords.add(args[i]);
    	}
    }

    OptionSet set = parser.parse(args);
    map.put("a", aCount.values(set));
    map.put("m", mCount.values(set));
    map.put("s", sCount.values(set));
    map.put("o", optionalWords);
	}

	public boolean isStopWord(String word) {
		return stoplist.contains(word);
	}

	public static void main(String[] args) {
		QueryParser qp = new QueryParser();
		Map<String, List<String>> newMap = qp.processArgument("Java -a programming -a tutorials google -m yay -s google.com");
		System.out.println("a: " + newMap.get("a"));
		System.out.println("m: " + newMap.get("m"));
		System.out.println("s: " + newMap.get("s"));
		System.out.println("o : " + newMap.get("o"));
	}
}