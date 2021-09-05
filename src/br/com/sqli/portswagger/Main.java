package br.com.sqli.portswagger;

import java.util.Scanner;

public class Main {


	public static void main(String[] args) throws InterruptedException {
	
		System.out.println("[+] Insira a URL: ");
		Scanner scan = new Scanner(System.in);
		String urlScanned = scan.nextLine();

		PerfomAttack attack = new PerfomAttack(urlScanned);
		System.out.println("[+] Trying retrive the password...");
		String pass = attack.execute();
		System.out.println("[+] The password is: " + pass);
		scan.close();
	}
}
