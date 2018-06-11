import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebSpider {

	// Make a mode where it only does domains, not urls in the domain
	// Make a real args parser
	// Save current urls to a file every 5 urls or something
	// Be able to import a url list of ones not to visit again

	// If the loop hits the max number of recursions, then
	// Save the urls
	// Clear the url list
	// set the recursion counter back to one
	// Keep going from the most recent set of urls

	// Settings start

	static boolean domainOnly = false;
	static boolean saveFile = false;
	static String fileName = "log.txt";
	static int recursions = Integer.MAX_VALUE;
	static int delay = 0;

	// Settings end

	private final Set<URL> links;
	private final long startTime;

	private WebSpider(final URL startURL) {
		this.links = new HashSet<>();
		this.startTime = System.currentTimeMillis();
		crawl(initURLS(startURL));
	}

	private Set<URL> initURLS(final URL startURL) {
		return Collections.singleton(startURL);
	}

	private void crawl(final Set<URL> urls) {
		// Remove the links we have already visited
		urls.removeAll(this.links);
		
		if (!urls.isEmpty()) {
			final Set<URL> newURLS = new HashSet<>();
			try {
				this.links.addAll(urls);
				for (final URL url : urls) {
					// Log the time
					if(System.currentTimeMillis() - this.startTime >= 1000) {
						System.out.println("[" + Math.round((System.currentTimeMillis() - this.startTime) / 1000) + " secconds] Connection: " + url);
					} else if(System.currentTimeMillis() - this.startTime >= 60000) {
						System.out.println("[" + Math.round((System.currentTimeMillis() - this.startTime) / 60000) + " hours] Connection: " + url);
					} else {
						System.out.println("[" + (System.currentTimeMillis() - this.startTime) + " ms] Connection: " + url);
					}

					// Get the web page
					final Document document = Jsoup.connect(url.toString()).get();

					// Select the links
					final Elements linksOnPage = document.select("a[href]");

					// Go to all of the urls on the page and add them to our list.
					for (final Element element : linksOnPage) {
						final String urlText = element.attr("abs:href");
						final URL discoveredURL = new URL(urlText);
						newURLS.add(discoveredURL);
					}
				}
			} catch (Exception e) {
				// Probably just a bad url but we want to keep crawling
			}
			
			crawl(newURLS);
		}
	}

	public static void help() {
		System.out.println("Insert help here.");
		System.exit(0);
	}

	public static void main(String[] args) {
//		for (String arg : args) {
			// I was going to do something here but now I forget
//		}
		
		// Check to make sure valid args were suplied
		if (args.length == 0 || !args[0].startsWith("http")) {
			System.out.println("Invalid args. WebSpider.jar {Start URL} {args}");
			System.exit(0);
		}
		
		// Handle the args
		// Sorry to everyone out there. I know this is really really bad, but I'm lazy and this is good enough.
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				switch (args[i]) {
				case "--help":
					help();
					break;
				case "-h":
					help();
					break;
				case "--domain":
					domainOnly = true;
					break;
				case "--save":
					saveFile = true;
					fileName = args[i + 1];
					break;
				case "-s":
					saveFile = true;
					fileName = args[i + 1];
					break;
				case "--recursion":
					recursions = Integer.parseInt(args[i + 1]);
					break;
				case "-r":
					recursions = Integer.parseInt(args[i + 1]);
					break;
				case "--delay":
					delay = Integer.parseInt(args[i + 1]);
					break;
				case "-d":
					delay = Integer.parseInt(args[i + 1]);
					break;
				}
			}
		}
		
		// Start the crawler
		try {
			new WebSpider(new URL(args[0]));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("An invalid URL has been detected.");
		}
	}
}
