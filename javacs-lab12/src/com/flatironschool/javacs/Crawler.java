package com.flatironschool.javacs;

import java.io.IOException;
import java.util.Queue;
import java.util.LinkedList;

import redis.clients.jedis.Jedis;

public class Crawler implements Runnable {
	private Thread thread;
	private Queue<String> queue = new LinkedList<String>();


	public static void main(String[] args) {
		System.out.println("Hello Crawler!");
	}
}