package net.arccotangent.kahoothack;

import java.util.ArrayList;
import java.util.Scanner;

public class App {
	
	private static int botsRunning(Kahoot[] bots) {
		int running = 0;
		for (Kahoot bot : bots) {
			if (bot.gameRunning())
				running++;
		}
		return running;
	}
	
	private static Kahoot[] clean(Kahoot[] bots) {
		ArrayList<Kahoot> kahoots = new ArrayList<>();
		for (Kahoot bot : bots) {
			if (bot.gameRunning())
				kahoots.add(bot);
		}
		Kahoot[] newbots = new Kahoot[kahoots.size()];
		kahoots.toArray(newbots);
		return newbots;
	}
	
	private static boolean q2Valid(Kahoot[] bots) {
		boolean valid = false;
		for (Kahoot bot : bots) {
			valid = bot.wasLastQuestionAnswer2Valid();
			if (valid)
				break;
		}
		return valid;
	}
	
	private static boolean q3Valid(Kahoot[] bots) {
		boolean valid = false;
		for (Kahoot bot : bots) {
			valid = bot.wasLastQuestionAnswer3Valid();
			if (valid)
				break;
		}
		return valid;
	}
	
	private static int getQuestionID(Kahoot[] bots) {
		int id;
		for (Kahoot bot : bots) {
			if (bot.gameRunning()) {
				return bot.getQuestionID();
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		int max_bps = 5; //Maximum bots allowed to connect per second
		
		System.out.print("Enter Game PIN: ");
		final Scanner stdin = new Scanner(System.in);
		final int gamepin = stdin.nextInt();
		stdin.nextLine(); //There is a newline character submitted with the int
		System.out.print("Checking game PIN validity... ");
		
		if (Session.checkPINValidity(gamepin)) {
			System.out.println("valid game PIN!");
		} else {
			System.out.println("invalid game PIN! Exiting.");
			return;
		}

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
			
			System.out.println((botz[0].isTeamGame() ? "Gamemode: TEAMS" : "Gamemode: CLASSIC PVP"));
			System.out.println("All bots are in game. While the bots are running, the main thread will print answer statistics.");

			int quid; //Question number

			int a = 0;
			int b = 0;
			int c = 0;
			int d = 0;
			//int invalid = 0;

			while (botsRunning(botz) >= 1) { //while at least 1 bot is still in the game...
				botz = clean(botz); //...clean out any kicked/errored bots...
				for (Kahoot bot : botz) { //...get all answers submitted by the remaining bots and count them up...
					try {
						int la = bot.getLastAnswerBlocking();
						if (la == 0) {
							a++;
						} else if (la == 1) {
							b++;
						} else if (la == 2) {
							c++;
						} else if (la == 3) {
							d++;
						//} else if (la == -1) {
						//	invalid++;
						} else {
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (botsRunning(botz) < 1) {
					break;
				}
				quid = getQuestionID(botz);
				System.out.println("---QUESTION " + quid + " STATISTICS---"); //..then display the statistics...
				System.out.println("Total bots: " + botz.length);
				System.out.println("Answer 0: " + a);
				System.out.println("Answer 1: " + b);
				System.out.println("Answer 2: " + c + (q2Valid(botz) ? "" : "(invalid answer)"));
				System.out.println("Answer 3: " + d + (q3Valid(botz) ? "" : "(invalid answer)"));
				//System.out.println("Invalid (disconnected/kicked from game): " + invalid);
				a = 0; //...and finally clear the variables for the next count
				b = 0;
				c = 0;
				d = 0;
				//invalid = 0;
			}

			System.out.println("The game appears to have ended. Exiting the program!");
			System.exit(0);
		} else {
			System.out.println("Invalid choice. Exiting.");
		}
	}

}