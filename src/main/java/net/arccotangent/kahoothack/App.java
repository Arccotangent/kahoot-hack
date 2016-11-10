package net.arccotangent.kahoothack;

import java.util.Scanner;

public class App {

	public static void main(String[] args) {
		int max_bps = 5; //Maximum bots allowed to connect per second
		
		System.out.print("Enter Game PIN: ");
		final Scanner stdin = new Scanner(System.in);
		final int gamepin = stdin.nextInt();
		stdin.nextLine(); //There is a newline character submitted with the int

		System.out.print("Username: ");
		String base = stdin.nextLine();

		System.out.println("[1] Play normally");
		System.out.println("[2] Flood with bots that randomly answer (If you join with enough bots, you're bound to win!)");
		System.out.print("Choice: ");
		int gm = stdin.nextInt();
		if (gm == 1) {
			Kahoot k = new Kahoot(base, gamepin, stdin, gm, false);
			k.initialize();
			System.out.println((k.isTeamGame() ? "Gamemode: TEAMS" : "Gamemode: CLASSIC PVP"));
			k.start();
		} else if (gm == 2) {
			System.out.print("Number of bots: ");
			int botCount = stdin.nextInt();
			System.out.println("Confirmation: Entering with " + botCount + " bots.");

			Kahoot[] botz = new Kahoot[botCount];

			for (int i = 0; i < botz.length; i++) {
				String name = base + (i + 1);
				botz[i] = new Kahoot(name, gamepin, stdin, gm, true); //Instantly activate Kahoot object when botting. Otherwise this leads to bugs.
				System.out.print("Initializing Kahoot bots: " + (i + 1) + " / " + botz.length + "\r");
				try {
					Thread.sleep(10); //Limit initializations to 100 bots per second
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("");
			
			System.out.println((botz[0].isTeamGame() ? "Gamemode: TEAMS" : "Gamemode: CLASSIC PVP"));

			for (int i = 0; i < botz.length; i++) {
				botz[i].start();
				System.out.print("Connecting Kahoot bots: " + (i + 1) + " / " + botz.length + "\r");
				try {
					Thread.sleep(1000 / max_bps); //Rate limit sign ins to max_bps bots per second
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("");

			System.out.println("All bots are in game. While the bots are running, the main thread will print answer statistics.");

			int quid = 0; //Question number

			int a = 0;
			int b = 0;
			int c = 0;
			int d = 0;

			while (botz[botz.length - 1].gameRunning()) { //while the last bot is still in the game...
				for (int i = 0; i < botz.length; i++) { //...get all answers submitted by the bots and count them up...
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
				System.out.println("---QUESTION " + quid + " STATISTICS---"); //..then display the statistics...
				System.out.println("Answer 0: " + a);
				System.out.println("Answer 1: " + b);
				System.out.println("Answer 2: " + c + (botz[botz.length - 1].wasLastQuestionAnswer2Valid() ? "" : "(invalid answer)"));
				System.out.println("Answer 3: " + d + (botz[botz.length - 1].wasLastQuestionAnswer3Valid() ? "" : "(invalid answer)"));
				a = 0; //...finally clear the variables for the next count
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