package com.flatironschool.javacs;



import java.io.File;

import java.io.FileNotFoundException;

import java.util.ArrayList;

import java.util.List;

import java.util.Random;

import java.util.Scanner;



public class RandomWord {

    RandomWord(){}

    public static void main(String[] args) throws FileNotFoundException{

    System.out.print(randomWords());

    }

    public static String randomWords() throws FileNotFoundException{

    	String token1 = "";

    	String token2 = "";

    	String slash = File.separator;

     	//String filename =  "/google-10000-english-usa.txt";

     	File file = new File("google-10000-english.txt");

        List<String> result = new ArrayList<String>();

        Scanner inFile1 = new Scanner(file);

        while (inFile1.hasNext()) {

              // find next line

              token1 = inFile1.next();

              result.add(token1);

            }

            inFile1.close();

            List<String> result2 = new ArrayList<String>();

        	   

            Random generator = new Random(); 

            int s = generator.nextInt(5) + 1;

            System.out.println(s);

            for(int i = 0; i < s; i++){

        	   	Random generator2 = new Random(); 

        	   	int f = generator2.nextInt(result.size()) + 1;

        	   	token2 = token2 + result.get(f) +" ";

            }

            return token2;
        }

}