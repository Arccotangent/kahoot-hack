package net.arccotangent.kahoothack;

import java.util.Scanner;

public class App {

	public static void main(String[] args) {
		System.out.print("Enter Game PIN: ");
		final Scanner stdin = new Scanner(System.in);
		final int gamepin = stdin.nextInt();
		stdin.nextLine();

		System.out.print("Username: ");
		String base = stdin.nextLine();

		System.out.println("[1] Play normally");
		System.out.println("[2] Flood with bots that randomly answer (If you join with enough bots, you're bound to win!)");
		System.out.print("Choice: ");
		int gm = stdin.nextInt();
		if (gm == 1) {
			Kahoot k = new Kahoot(base, stdin, gm, false);
			k.initialize(gamepin);
			k.start();
		} else if (gm == 2) {
			System.out.print("Number of bots: ");
			int botCount = stdin.nextInt();
			System.out.println("Confirmation: Entering with " + botCount + " bots.");

			Kahoot[] botz = new Kahoot[botCount];

			for (int i = 0; i < botz.length; i++) {
				String name = base + (i + 1);
				botz[i] = new Kahoot(name, stdin, gm, true);
				botz[i].initialize(gamepin);
			}

			for (int i = 0; i < botz.length; i++) {
				botz[i].start();
			}

			System.out.println("All bots are in game. While the bots are running, the main thread will print answer statistics.");

			int quid = 0;

			int a = 0;
			int b = 0;
			int c = 0;
			int d = 0;

			while (botz[botz.length - 1].gameRunning()) { //while the last bot is still in the game
				for (int i = 0; i < botz.length; i++) {
					try {
						int la = botz[i].getLastAnswerBlocking();
						if (la == 0) {
							a++;
						} else if (la == 1) {
							b++;
						} else if (la == 2) {
							c++;
						} else if (la == 3) {
							d++;
						} else {
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (!botz[botz.length - 1].gameRunning()) {
					break;
				}
				quid++;
				System.out.println("---QUESTION " + quid + " STATISTICS---");
				System.out.println("Answer 0: " + a);
				System.out.println("Answer 1: " + b);
				System.out.println("Answer 2: " + c + (botz[botz.length - 1].wasLastQuestionAnswer2Valid() ? "" : "(invalid answer)"));
				System.out.println("Answer 3: " + d + (botz[botz.length - 1].wasLastQuestionAnswer3Valid() ? "" : "(invalid answer)"));
				a = 0;
				b = 0;
				c = 0;
				d = 0;
			}

			System.out.println("The game appears to have ended. Exiting the program!");
			System.exit(0);
		} else {
			System.out.println("Invalid choice. Exiting.");
		}
	}

}