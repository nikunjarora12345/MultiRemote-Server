package com.kaminari.mutiremote;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

	private static ServerSocket server = null;
	private static Socket client = null;
	private static BufferedReader in = null;
	private static String line = null;
	private static boolean isConnected = true;
	private static Robot robot = null;
	private static final int SERVER_PORT = 6789;

	public static void main(String[] args) {
		try {
			System.out.println("Type in the following address on the Phone: "
					+ InetAddress.getLocalHost().getHostAddress()); // Gets the
																	// IP
																	// address
																	// of the
																	// current
																	// machine
			robot = new Robot(); // Robot is used to simulate keyboard and mouse
									// events
			server = new ServerSocket(SERVER_PORT); // Creates a server on port
													// 6789
			client = server.accept(); // Program is stopped until a client
										// connects

			System.out.println(client.getInetAddress().getHostAddress()
					+ " connected!"); // Print the IP address of the connected
										// client

			in = new BufferedReader(new InputStreamReader(
					client.getInputStream())); // Input stream from where data
												// is received from client
		} catch (AWTException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// read input from client while it is connected
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (isConnected) {
					try {

						if (in.ready()) {
							line = in.readLine(); // read input from client
							System.out.println("Recieved input: " + line);

							// Simulate mouse events
							if (line.equalsIgnoreCase("left_click")) {

								robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
								robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

							} else if (line.equalsIgnoreCase("right_click")) {

								robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
								robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);

							} else if (line.equalsIgnoreCase("middle_click")) {

								robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
								robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);

							} else if (line.contains(",")) {

								float mouseX = Float.parseFloat(line.split(",")[0]); // Extract
																						// the
																						// number
																						// before
																						// comma

								float mouseY = Float.parseFloat(line.split(",")[1]); // Extract
																						// the
																						// number
																						// after
																						// comma

								Point point = MouseInfo.getPointerInfo()
										.getLocation(); // Get
														// the
														// current
														// pointer
														// location

								float nowX = point.x;
								float nowY = point.y;

								robot.mouseMove((int) (nowX + mouseX),
										(int) (nowY + mouseY)); // Move the
																// pointer from
																// the current
																// position

							} else if (line.contains("scroll")) {

								int notches = (int) Math.floor(Double
										.parseDouble(line.substring(7))); // Extract
																			// the
																			// number
																			// from
																			// string
																			// "scroll:#number"
								robot.mouseWheel(notches);

							} else if (line.contains("press")) {

								String[] parts = line.split("_");
								robot.keyPress(Integer.parseInt(parts[0]));

							} else if (line.contains("release")) {

								String[] parts = line.split("_");
								robot.keyRelease(Integer.parseInt(parts[0]));

							} else if (line.equalsIgnoreCase("exit")) {

								isConnected = false;

								// Close server and client
								server.close();
								client.close();
								in.close();
							}
						}

					} catch (IOException e) {
						e.printStackTrace();
						System.exit(-1);
					} catch (NullPointerException e) {
						isConnected = false;
						System.exit(-1);
					}
				}
			}
		}).run();

	}

}
