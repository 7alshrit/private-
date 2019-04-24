package Mydraw;
// This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)

// Copyright (c) 1997 by David Flanagan
// This example is provided WITHOUT ANY WARRANTY either expressed or implied.
// You may study, use, modify, and distribute it for non-commercial purposes.
// For any commercial use, see http://www.davidflanagan.com/javaexamples

import java.applet.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

/** The application class. Processes high-level commands sent by GUI */
public class Draw {
	/** main entry point. Just create an instance of this application class */
	public static void main(String[] args) {
		new Draw();
	}

	/** Application constructor: create an instance of our GUI class */
	public Draw() {
		window = new DrawGUI(this);
	}
	
	public DrawGUI getWindow()
	{
		return window;
	}

	protected DrawGUI window;

	/** This is the application method that processes commands sent by the GUI */
	public void doCommand(String command) {
		if (command.equals("clear")) { // clear the GUI window
			// It would be more modular to include this functionality in the GUI
			// class itself. But for demonstration purposes, we do it here.
			//Graphics g = window.getGraphics();
			//g.setColor(window.getBackground());
			//g.fillRect(0, 0, window.getSize().width, window.getSize().height);
			window.clear();
		} else if (command.equals("quit")) { // quit the application
			window.dispose(); // close the GUI
			System.exit(0); // and exit.
		}
	}
}


