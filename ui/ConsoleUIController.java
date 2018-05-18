package geoapp.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import geoapp.App;
import geoapp.CompositeRoute;
import geoapp.GeoPosition;
import geoapp.Waypoint;

/**
 * An example UI built from the UIController interface.
 * Utilises a console instead of a GUI.
 */
public class ConsoleUIController implements UIController, Runnable
{
	// True if the routes have been downloaded from the server
	boolean routesDownloaded = false;
	
	public static void main(String[] args)
	{
		System.out.println("Starting app...");
		ConsoleUIController controller = new ConsoleUIController();
		App.init(controller);
		Thread uiThread = new Thread(controller);
		uiThread.start();
		
		
	}

	private void routeInfo(String name)
	{
		for(CompositeRoute route : App.getRoutes())
		{
			if(route.toString().equals(name))
			{
				ArrayList<Waypoint> points = route.getWaypoints();
				
				// This is the route we're searching for
				System.out.println("Name: " + route);
				System.out.println("Description:\n    " + route.getDescription());
				System.out.println("Waypoints: " + points.size());
				for(Waypoint point : points)
				{
					System.out.println("    " + point);
				}
				DecimalFormat fm = new DecimalFormat("###,###.#");
				System.out.println(
						"Distance:\n    "   + fm.format(route.getHorizontalDistance()) + "m" +
						"\nVertical ascent:\n    " + fm.format(route.getAscension()) + "m" +
						"\nVertical descent:\n    " + fm.format(route.getDescension()) + "m"
					);
			}
		}
	}
	
	
	@Override
	public void positionUpdated(GeoPosition position)
	{
		if(position != null)
		{
			System.out.println("Current position: " + position);
		}else
		{
			System.out.println("Current position unavailable.");
		}
	}

	@Override
	public void routesUpdated(ArrayList<CompositeRoute> routes)
	{
		if(!routesDownloaded)
		{
			System.out.println("Found " + routes.size() + " routes:");
			for(CompositeRoute route : routes)
			{
				System.out.println(route + ": '" + route.getDescription() + "'");
			}
			routesDownloaded = true;
		}
		
	}

	@Override
	public void run() {

		System.out.println("This is a console wrapper for the app, a set of commands can be shown by typing 'help'.");
		String[] userInput;
		Scanner scr = new Scanner(System.in);
		boolean enabled = true;
		while(enabled)
		{
			userInput = scr.nextLine().split(" ");
			if(userInput.length > 0)
			{
				switch(userInput[0])
				{
					case "exit":
						enabled = false;
						System.out.println("Bye =)");
						break;
					case "help":
						System.out.println("help         - Shows this dialog");
						System.out.println("exit         - Exits the program");
						System.out.println("info [name]  - Shows information about a route with the given name.");
						System.out.println("list         - Lists all currently available routes.");
						break;
					case "info":
						if(userInput.length > 1)
						{
							routeInfo(userInput[1]);
						}
						break;
					case "list":
						listRoutes();
						break;
				}
			}
		}
		scr.close();
	}
	
	void listRoutes()
	{
		for(CompositeRoute route : App.getRoutes())
		{
			System.out.println(route + ": " + route.getDescription());
		}
	}

	@Override
	public void targetWaypointUpdated(Waypoint newTarget)
	{
		System.out.println("Waypoint reached. New target: " + newTarget);
	}

	@Override
	public void routeDownloadFailed()
	{
		System.out.println("Failed to get routes from server.");
	}

}
