# cs-application-retrieval-lab

## Learning goals

1.  Analyze the performance of Web indexing algorithms.
2.  Use boolean operators to generate search results for multiple search terms.
3.  Score search results by relevance and sort them.


## Overview

In this lab, we present our solution to the previous lab.  Then you will write code to combine multiple search results and sort them by their relevance to the search terms.


## Crawler solution

First, let's go over our solution to the previous lab.  We provided an outline of `WikiCrawler`; your job was to fill in `crawl`.  As a reminder, here are the fields in the `WikiCrawler` class:

```java
public class WikiCrawler {
	// keeps track of where we started
	private final String source;

	// the index where the results go
	private JedisIndex index;

	// queue of URLs to be indexed
	private Queue<String> queue = new LinkedList<String>();

	// fetcher used to get pages from Wikipedia
	final static WikiFetcher wf = new WikiFetcher();
}
```

When we create a `WikiCrawler`, we provide `source` and `index`.  Initially, `queue` contains only one element, `source`.

Notice that the implementation of `queue` is a `LinkedList`, so we can add elements at the end -- and remove them from the beginning -- in constant time.  By assigning a `LinkedList` object to a `Queue` variable, we limit ourselve to using methods in the `Queue` interface; specifically, we'll use `offer` to add elements and `poll` to remove them.

Here's our implementation of `crawl`:

```java
	public String crawl(boolean testing) throws IOException {
		if (queue.isEmpty()) {
			return null;
		}
		String url = queue.poll();
		System.out.println("Crawling " + url);

		if (testing==false && index.isIndexed(url)) {
			System.out.println("Already indexed.");
			return null;
		}

		Elements paragraphs;
		if (testing) {
			paragraphs = wf.readWikipedia(url);
		} else {
			paragraphs = wf.fetchWikipedia(url);
		}
		index.indexPage(url, paragraphs);
		queueInternalLinks(paragraphs);
		return url;
	}
```

Most of the complexity in this method is there to make it easier to test.  Here's the logic:

*  If the queue is empty, it returns `null` to indicate that it did not index a page.

*  Otherwise it removes and stores the next URL from the queue.

*  If the URL has already been indexed, `crawl` doesn't index it again, unless it's in testing mode.

*  Next it reads the contents of the page: if it's in testing mode, it reads from a file; otherwise it reads from the Web.

*  It indexes the page.

*  It parses the page and adds internal links to the queue.

*  Finally, it returns the URL of the page it indexed.

We presented our implementation of `index.indexPage` in the previous lab.  So the only new function is `queueInternalLinks`.

We wrote two versions of this function with different parameters: one takes an `Elements` object containing one DOM trees per paragraph; the other takes an `Element` object that contains a single paragraph.

The first version just loops through the paragraphs.  The second version does the real work.

```java
	void queueInternalLinks(Elements paragraphs) {
		for (Element paragraph: paragraphs) {
			queueInternalLinks(paragraph);
		}
	}

	private void queueInternalLinks(Element paragraph) {
		Elements elts = paragraph.select("a[href]");
		for (Element elt: elts) {
			String relURL = elt.attr("href");

			if (relURL.startsWith("/wiki/")) {
				String absURL = elt.attr("abs:href");
				queue.offer(absURL);
			}
		}
	}
```

To determine whether a link is "internal," we check whether the URL starts with "/wiki/".  This might include some pages we don't want to index, like meta-pages about Wikipedia.  And it might exclude some pages we want, like links to pages in non-English languages.  But we kept it simple.

That's all there is to it.  This lab didn't have a lot of new material; it was mostly a chance to bring the pieces together.


## Information retrieval

The next phase of this project is to implement a search tool.  The pieces we'll need include:

1.  An interface where users can provide search terms and view results.

2.  A lookup mechanism that takes each search term and returns the pages that contain it.

3.  Mechanisms for combining search results from multiple search terms.

4.  Algorithms for ranking and sorting search results.

The general term for processes like this is "information retrieval", [which you can read more about here](https://en.wikipedia.org/wiki/Information_retrieval).

In this lab, we'll focus on steps 3 and 4.  We've already build a simple version of 2.  If you are interested in building Web applications, you'll have to option to work on step 1.



## Boolean search

Most search engines can perform "boolean searches", which means you can combine the results from multiple search terms using boolean logic.  For example:

*  The search "java AND programming" might return only pages that contain both search terms: "java" and "programming".

*  "java OR programming" might return pages that contain either term but not necessarily both.

*  "java -indonesia" might return pages that contain "java" and do not contain "indonesia".

Expressions like these that contain search terms and operators are called "queries".

When applied to search results, the boolean operators `AND`, `OR`, and `-` correspond to the set operations `intersection`, `union`, and `difference`.  For example, suppose

*  `s1` is the set of pages containing "java",

*  `s2` is the set of pages containing "programming", and

*  `s3` is the set of pages containing "indonesia".

In that case:

*  The intersection of `s1` and `s2` is the set of pages containing "java" AND "programming".

*  The union of `s1` and `s2` is the set of pages containing "java" OR "programming".

*  The difference of `s1` and `s2` is the set of pages containing "java" and not "indonesia".

In the next section you will write method to implement these operations.


## Relevance scores

When you check out the repository for this lab, you should find a file structure similar to what you saw in previous labs.  The top level directory contains `CONTRIBUTING.md`, `LICENSE.md`, `README.md`, and the directory with the code for this lab, `javacs-lab12`.

In the subdirectory `javacs-lab12/src/com/flatironschool/javacs` you'll find the source files for this lab:

*  `WikiSearch.java`, which defines an object that contains search results and performs operations on them.

*  `WikiSearchTest.java`, which contains test code for `WikiSearch`.

*  `Card.java`, which demonstrates how to use the `sort` method in `java.util.Collections`.

You will also find some of the helper classes we've used in previous labs.

Here's the beginning of the `WikiSearch` class definition:

```java
public class WikiSearch {

	// map from URLs that contain the term(s) to relevance score
	private Map<String, Integer> map;

	public WikiSearch(Map<String, Integer> map) {
		this.map = map;
	}

	public Integer getRelevance(String url) {
		Integer relevance = map.get(url);
		return relevance==null ? 0: relevance;
	}
```

A `WikiSearch` object contains a map from URLs to their relevance score.  In the context of information retrieval, a "relevance score" is a number intended to indicate how well a page meets the needs of the user as inferred from the query.  There are many ways to construct a relevance score, but most of them are based on "term frequency", which is the number of times the search terms appear on the page.  A common relevance score is called TF-IDF, which stands for "term frequency -- inverse document frequency".  [You can read more about it here](https://en.wikipedia.org/wiki/Tf%E2%80%93idf).

You'll have the option to implement TF-IDF later, but we'll start with something even simpler, TF:

*  If a query contains a single search term, the relevance of a page is its term frequency; that is, the number of time the term appears on the page.

*  For queries with multiple terms, the revelance of a page is the sum of the term frequencies; that is, the total number of times any of the search terms appear.

Now you're ready to start the lab.


## Implementing boolean operators

Assuming you have already checked out the repository for this lab, you should have a directory named `javacs-lab12` that contains the Ant build file `build.xml`.

Run `ant build` to compile the source files, then run `ant test` to run `WikiSearchTest`.  As usual, it should fail, because you have work to do.

In `WikiSearch.java`, fill in the bodies of `and`, `or`, and `minus` so that the relevant tests pass.  You don't have to worry about `testSort` yet.

You can run `WikiSearchTest` without using Jedis because it doesn't depend on the index in your Redis database.  But if you want to run a query against your index, you have to provide a file with information about your Redis server.  If you did this in the previous lab, you can just copy it over.  Otherwise you can find instructions in [cs-application-backing-with-redis-lab](ADD THIS LINK AFTER DEPLOYMENT).

Run `ant JedisMaker` to make sure it is configured to connect to your Redis server.  Then run `WikiSearch`, which prints results from three queries:

*  "java"

*  "programming"

*  "java AND programming

Initially the results will be in no particular order, because `WikiSearch.sort` is incomplete.

Fill in the body of `sort` so the results are returned in increasing order of relevance.  We suggest you use the `sort` method provided by [`java.util.Collections`](https://docs.oracle.com/javase/7/docs/api/java/util/Collections.html), which sorts any kind of `List`.  There are two version of `sort`:

* The one-parameter version takes a list and sorts the elements using the `compareTo` method, so the elements have to be `Comparable`.

* The two-parameter version takes a list of any object type and a `Comparator`, which is an object that provides a `compare` method that compares elements.

If you are not familiar with the `Comparable` and `Comparator` interfaces, we explain them in the next section.


## `Comparable` and `Comparator`

This labs includes `Card.java`, which demonstrates two ways to sort a list of `Card` objects.  Here's the beginning of the class definition:

```java
public class Card implements Comparable<Card> {

	private final int rank;
    private final int suit;

    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }
```

A `Card` object has two integer fields, `rank` and `suit`.  `Card` implements `Comparable<Card>`, which means that it provides `compareTo`:


```java
    public int compareTo(Card that) {
        if (this.suit < that.suit) {
            return -1;
        }
        if (this.suit > that.suit) {
            return 1;
        }
        if (this.rank < that.rank) {
            return -1;
        }
        if (this.rank > that.rank) {
            return 1;
        }
        return 0;
    }
```

The specification of `compareTo` indicates that it should return a negative number if `this` is considered less than `that`, a positive number if it is considered greater, and 0 if they are considered equal.

If you use the one-parameter version of `Collections.sort`, it uses the `compareTo` method provided by the elements to sort them.  To demonstrate, we can make a list of 52 cards like this:

```java
    public static List<Card> makeDeck() {
        List<Card> cards = new ArrayList<Card>();
        for (int suit = 0; suit <= 3; suit++) {
            for (int rank = 1; rank <= 13; rank++) {
                Card card = new Card(rank, suit);
                cards.add(card);
            }
        }
        return cards;
    }
```

And sort them like this:

```java
        Collections.sort(cards);
```

This version of `sort` puts the elements in what's called their "natural order" because it's determined by the objects themselves.

But it is possible to impose a different ordering by providing a `Comparator` object.  For example, the natural order of `Card` objects treats Aces as the lowest rank, but in some card games they have the highest rank.  We can define a `Comparator` that considers "Aces high", like this:

```java
        Comparator<Card> comparator = new Comparator<Card>() {
            @Override
            public int compare(Card card1, Card card2) {
            	if (card1.getSuit() < card2.getSuit()) {
                    return -1;
                }
                if (card1.getSuit() > card2.getSuit()) {
                    return 1;
                }
                int rank1 = getRankAceHigh(card1);
                int rank2 = getRankAceHigh(card2);

                if (rank1 < rank2) {
                    return -1;
                }
                if (rank1 > rank2) {
                    return 1;
                }
                return 0;
            }

			private int getRankAceHigh(Card card) {
				int rank = card.getRank();
				if (rank == 1) {
					return 14;
				} else {
					return rank;
				}
			}
        };
```

This code defines an anonymous class that implements `compare`, as required.  Then it creates an instance of the class.  If you are not familiar with anonymous classes in Java, [you can read about them here](https://docs.oracle.com/javase/tutorial/java/javaOO/anonymousclasses.html).

Using this `Comparator`, we can invoke `sort` like this:

```java
		Collections.sort(cards, comparator);
```

In this ordering, the Ace of Spaces is considered the highest class in the deck; the two of clubs is the lowest.

The code in this section is in `Card.java` if you want to experiment with it.  As an exercise, you might want to write a comparator that sorts by `rank` first and then by `suit`, so all the Aces should be together, and all the twos, etc.


## Extensions

If you get a basic version of this lab working, you might want to work on these optional exercises:

* [Read about TF-IDF](https://en.wikipedia.org/wiki/Tf%E2%80%93idf) and implement it.  You might have to modify `JedisIndex` to compute document frequencies; that is, the total number of times each term appears on all pages in the index.

* For queries with more than one search term, the total relevance for each page is currently the sum of the relevance for each term.  Think about when this simple version might not work well, and try out some alternatives.

* Build a user interface that allows users to enter queries with boolean operators.  Parse the queries, generate the results, then sort them by relevance and display the highest-scoring URLs.  Consider generating "snippets" that show where the search terms appeared on the page.  If you want to make a Web application for your user interface, we recommend [Heroku](https://devcenter.heroku.com/articles/getting-started-with-java) as simple options for developing and deploying Web applications using Java.



## Resources

*  [Information retrieval](https://en.wikipedia.org/wiki/Information_retrieval).

*  [TF-IDF](https://en.wikipedia.org/wiki/Tf%E2%80%93idf)

*  [Anonymous classes](https://docs.oracle.com/javase/tutorial/java/javaOO/anonymousclasses.html)

<p class='util--hide'>View <a href='https://learn.co/lessons/cs-application-retrieval-lab'>Retrieval Lab</a> on Learn.co and start learning to code for free.</p>
