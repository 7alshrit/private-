package Mydraw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** This class implements the GUI for our application */
public class DrawGUI extends JFrame {
	Draw app; // A reference to the application, to send commands to.
	Color color;
	Color colorBG;
	String[] colors;
	String[] colorsBG;
	BufferedImage drawing;
	JPanel panel;

	
	/**
	 * The GUI constructor does all the work of creating the GUI and setting up
	 * event listeners. Note the use of local and anonymous classes.
	 */
	public DrawGUI(Draw application) {
		super("Draw"); // Create the window
		app = application; // Remember the application reference
		color = Color.black; // the current drawing color
		colorBG = Color.white;
		colors = new String[4];
		colors[0]="black";
		colors[1]="red";
		colors[2]="green";
		colors[3]="blue";
		
		colorsBG = new String[5];
		colorsBG[0]="white";
		colorsBG[1]="red";
		colorsBG[2]="green";
		colorsBG[3]="blue";
		colorsBG[4]="black";
		
		int width = 750;
		int height = 500;
		
		drawing = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(width,height));
		panel.setBackground(Color.white);

		// selector for drawing modes
		String[] shapes = {"Scribble", "Rectangle", "Oval"};
		JComboBox<String> shape_chooser = new JComboBox<String>(shapes);

		// selector for drawing colors
		JComboBox<String> color_chooser = new JComboBox<String>(colors);

		JComboBox<String> colorBG_chooser = new JComboBox<String>(colorsBG);
		
		// Create two buttons
		JButton clear = new JButton("Clear");
		JButton quit = new JButton("Quit");
		JButton auto = new JButton("Auto");
		JButton save = new JButton("Save");
		JButton open = new JButton("Open");

		// Set a LayoutManager, and add the choosers and buttons to the window.
		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.CENTER);
		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.white);
		this.add(topPanel, BorderLayout.NORTH);
		
		topPanel.add(new JLabel("Shape:"));
		topPanel.add(shape_chooser);
		topPanel.add(new JLabel("Color:"));
		topPanel.add(color_chooser);
		topPanel.add(new JLabel("Background:"));
		topPanel.add(colorBG_chooser);
		
		topPanel.add(save);
		topPanel.add(open);
		topPanel.add(auto);
		topPanel.add(clear);
		topPanel.add(quit);

		// Here's a local class used for action listeners for the buttons
		class DrawActionListener implements ActionListener {
			private String command;

			public DrawActionListener(String cmd) {
				command = cmd;
			}

			public void actionPerformed(ActionEvent e) {
				app.doCommand(command);
			}
		}
		
		class AutoActionListener implements ActionListener
		{

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				autoDraw();
			}
			
		}
		
		class SaveActionListener implements ActionListener
		{
			DrawGUI gui;
			public SaveActionListener(DrawGUI drawGUI) {
				gui = drawGUI;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(gui, "Enter a name for the .bmp file.",
						"Save as", JOptionPane.PLAIN_MESSAGE);
				
				if (name != null && name.matches("[a-zA-Z_0-9]+"))
				{
					try {
						writeImage(getDrawing(),name);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(gui, "The file wasn't succesfully saved.",
								"Saving Error", JOptionPane.WARNING_MESSAGE);
					}
				}
				else if (name != null)
				{
					JOptionPane.showMessageDialog(gui, "The filename wasn't accepted.",
							"Naming Error", JOptionPane.WARNING_MESSAGE);
				}

			}
			
		}
		
		class OpenActionListener implements ActionListener
		{
			DrawGUI gui;
			public OpenActionListener(DrawGUI drawGUI) {
				gui = drawGUI;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(gui, "Enter the name of the .bmp file you want to open.",
						"Open", JOptionPane.PLAIN_MESSAGE);
				
				if (name != null && name.matches("[a-zA-Z_0-9]+"))
				{
					try {
						nextImage(readImage(name));
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(gui, "The file doesn't exist or can't be read.",
								"Reading Error", JOptionPane.WARNING_MESSAGE);
					}
				}
				else if (name != null)
				{
					JOptionPane.showMessageDialog(gui, "The filename wasn't accepted.",
							"Naming Error", JOptionPane.WARNING_MESSAGE);
				}
			}
			
		}

		// Define action listener adapters that connect the buttons to the app
		clear.addActionListener(new DrawActionListener("clear"));
		quit.addActionListener(new DrawActionListener("quit"));
		auto.addActionListener(new AutoActionListener());
		save.addActionListener(new SaveActionListener(this));
		open.addActionListener(new OpenActionListener(this));

		// this class determines how mouse events are to be interpreted,
		// depending on the shape mode currently set
		class ShapeManager implements ItemListener {
			DrawGUI gui;

			abstract class ShapeDrawer extends MouseAdapter implements MouseMotionListener {
				public void mouseMoved(MouseEvent e) {
					/* ignore */ }
			}

			// if this class is active, the mouse is interpreted as a pen
			class ScribbleDrawer extends ShapeDrawer {
				int lastx, lasty;

				public void mousePressed(MouseEvent e) {
					lastx = e.getX();
					lasty = e.getY();
				}

				public void mouseDragged(MouseEvent e) {
					Image newDrawing = copyDrawing();
					Graphics g = newDrawing.getGraphics();
					int x = e.getX(), y = e.getY();
					g.setColor(gui.color);
					g.setPaintMode();
					g.drawLine(lastx, lasty, x, y);
					lastx = x;
					lasty = y;
					nextImage(newDrawing);
				}
			}

			// if this class is active, rectangles are drawn
			class RectangleDrawer extends ShapeDrawer {
				int pressx, pressy;
				int lastx = -1, lasty = -1;

				// mouse pressed => fix first corner of rectangle
				public void mousePressed(MouseEvent e) {
					pressx = e.getX();
					pressy = e.getY();
				}

				// mouse released => fix second corner of rectangle
				// and draw the resulting shape
				public void mouseReleased(MouseEvent e) {
					Image newDrawing = copyDrawing();
					Graphics g = newDrawing.getGraphics();
					if (lastx != -1) {
						// first undraw a rubber rect
						g.setXORMode(gui.color);
						g.setColor(gui.getBackground());
						doDraw(pressx, pressy, lastx, lasty, g);
						lastx = -1;
						lasty = -1;
					}
					// these commands finish the rubberband mode
					g.setPaintMode();
					g.setColor(gui.color);
					// draw the finel rectangle
					doDraw(pressx, pressy, e.getX(), e.getY(), g);
					nextImage(newDrawing);				
				}

				// mouse released => temporarily set second corner of rectangle
				// draw the resulting shape in "rubber-band mode"
				public void mouseDragged(MouseEvent e) {
					Image newDrawing = copyDrawing();
					Graphics g = newDrawing.getGraphics();
					// these commands set the rubberband mode
					g.setXORMode(gui.color);
					g.setColor(gui.getBackground());
					if (lastx != -1) {
						// first undraw previous rubber rect
						doDraw(pressx, pressy, lastx, lasty, g);

					}
					lastx = e.getX();
					lasty = e.getY();
					// draw new rubber rect
					doDraw(pressx, pressy, lastx, lasty, g);
					nextImage(newDrawing);
				}

				public void doDraw(int x0, int y0, int x1, int y1, Graphics g) {
					// calculate upperleft and width/height of rectangle
					int x = Math.min(x0, x1);
					int y = Math.min(y0, y1);
					int w = Math.abs(x1 - x0);
					int h = Math.abs(y1 - y0);
					// draw rectangle
					g.drawRect(x, y, w, h);
				}
			}

			// if this class is active, ovals are drawn
			class OvalDrawer extends RectangleDrawer {
				public void doDraw(int x0, int y0, int x1, int y1, Graphics g) {
					int x = Math.min(x0, x1);
					int y = Math.min(y0, y1);
					int w = Math.abs(x1 - x0);
					int h = Math.abs(y1 - y0);
					// draw oval instead of rectangle
					g.drawOval(x, y, w, h);
				}
			}

			ScribbleDrawer scribbleDrawer = new ScribbleDrawer();
			RectangleDrawer rectDrawer = new RectangleDrawer();
			OvalDrawer ovalDrawer = new OvalDrawer();
			ShapeDrawer currentDrawer;

			public ShapeManager(DrawGUI itsGui) {
				gui = itsGui;
				// default: scribble mode
				currentDrawer = scribbleDrawer;
				// activate scribble drawer
				panel.addMouseListener(currentDrawer);
				panel.addMouseMotionListener(currentDrawer);
			}

			// reset the shape drawer
			public void setCurrentDrawer(ShapeDrawer l) {
				if (currentDrawer == l)
					return;

				// deactivate previous drawer
				panel.removeMouseListener(currentDrawer);
				panel.removeMouseMotionListener(currentDrawer);
				// activate new drawer
				currentDrawer = l;
				panel.addMouseListener(currentDrawer);
				panel.addMouseMotionListener(currentDrawer);
			}

			// user selected new shape => reset the shape mode
			public void itemStateChanged(ItemEvent e) {
				if (e.getItem().equals("Scribble")) {
					setCurrentDrawer(scribbleDrawer);
				} else if (e.getItem().equals("Rectangle")) {
					setCurrentDrawer(rectDrawer);
				} else if (e.getItem().equals("Oval")) {
					setCurrentDrawer(ovalDrawer);
				}
			}
		}

		shape_chooser.addItemListener(new ShapeManager(this));

		class ColorItemListener implements ItemListener {

			// user selected new color => store new color in DrawGUI
			public void itemStateChanged(ItemEvent e) {
				if (e.getItem().equals(colors[0])) {
					color = Color.black;
				} else if (e.getItem().equals(colors[1])) {
					color = Color.red;
				} else if (e.getItem().equals(colors[2])) {
					color = Color.green;				
				} else if (e.getItem().equals(colors[3])) {
					color = Color.blue;
				}
				
			}
		}

		color_chooser.addItemListener(new ColorItemListener());
		
		class ColorBGItemListener implements ItemListener {

			public void itemStateChanged(ItemEvent e) {
				
				for (String c: colorsBG)
				{
					if (e.getItem().equals(c))
					{
						try {
							setBGColor(c);
						} catch (ColorException e1) {
							System.out.println("Farbe konnte nicht gefunden werden.");
						}
					}
				}
			}
		}
		
		colorBG_chooser.addItemListener(new ColorBGItemListener());

		// Handle the window close request similarly
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				app.doCommand("quit");
			}
		});

		// Finally, set the size of the window, and pop it up
		this.pack();
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) 
			{
				nextImage(drawing);
            }
		});
		this.setMinimumSize(this.getSize());
		this.setBackground(colorBG);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}	
	
	
	public void autoDraw()
	{
		try {
			setFGColor("red");
		} catch (ColorException e1) {
			System.out.println("Farbe konnte nicht gefunden werden.");
		}
		try {
			setBGColor("black");
		} catch (ColorException e1) {
			System.out.println("Farbe konnte nicht gefunden werden.");
		}
		drawRectangle(new Point(0,0), new Point(200,300));
		drawOval(new Point(200,200), new Point(400,350));
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(0,100));
		points.add(new Point(150,250));
		points.add(new Point(150,100));
		
		drawPolyLine(points);
	}
	
	
	/**
	 * Abfrage der Zeichenfarbe 
	 */
	public String getFGColor() {
		if (color.equals(Color.black))
		{
			return "black";
		}
		else if (color.equals(Color.green))
		{
			return "green";
		}
		else if (color.equals(Color.red))
		{
			return "red";
		}
		else if (color.equals(Color.blue))
		{
			return "blue";
		}
		return "";
	}
	
	/**
	 * Setzen der Zeichenfarbe 
	 */
	public void setFGColor(String new_color) throws ColorException {
		
		String c = new_color.toLowerCase();
		ArrayList<String> colorList = new ArrayList<String>();
		for (String s : colors)
		{
			colorList.add(s);
		}
		if (colorList.contains(c))
		{
			if (c.equals("black")) {
				color = Color.black;
			} else if (c.equals("green")) {
				color = Color.green;
			} else if (c.equals("red")) {
				color = Color.red;
			} else if (c.equals("blue")) {
				color = Color.blue;
			}
		}
		else
		{
			throw new ColorException();
		}
		
	}
	
	/**
	 * Abfrage der Groesse der Zeichenflaeche
	 */
	public int getWidth() {
		
		return panel.getWidth();
	}
	
	
	
	/**
	 * Abfrage der Groesse der Zeichenflaeche
	 */
	public int getHeight() {
		return panel.getHeight();
	}
	
	
	/**
	 * setzen der Größe der Zeichenfläche (Width)
	 * @param new_Width
	 */
	public void setWidth (int width) {
		  
		 panel.setSize(width, getHeight());
		 
	 }
	
	
	 /**
	* setzen der Größe der Zeichenfläche (Hight)
	 * @param new_Height
	 */
	public void setHeight (int height) {
		panel.setSize(getWidth(), height);
					 
		 }
	
	/**
	 * Setzen der Hintergrunddfarbe 
	 * @param new_color
	 */
	
	public void setBGColor (String new_color) throws ColorException
	{
		Color altColorBG = colorBG;
		String c = new_color.toLowerCase();
		ArrayList<String> colorList = new ArrayList<String>();
		for (String s : colors)
		{
			colorList.add(s);
		}
		if (colorList.contains(c) || new_color.equals("white"))
		{
			if (c.equals("black")) {
				colorBG = Color.black;				
			} else if (c.equals("green")) {
				colorBG = Color.green;
			} else if (c.equals("red")) {
				colorBG = Color.red;
			} else if (c.equals("blue")) {
				colorBG = Color.blue;
			} else if (c.equals("white")) {
				colorBG = Color.white;
			}
			Image altDrawing = copyDrawing();
			BufferedImage newDrawing = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = newDrawing.getGraphics();
			g.drawImage(altDrawing, 0, 0, null);
			
			for (int i=0; i < newDrawing.getWidth()-1; i++)
			{
				for (int j=0; j < newDrawing.getHeight()-1; j++)
				{
					int rgb = newDrawing.getRGB(i, j);
					if (rgb == altColorBG.getRGB())
					{
						newDrawing.setRGB(i, j, colorBG.getRGB());
					}
				}
			}
			
			nextImage(newDrawing);
		}
		else
		{
			throw new ColorException();
		}
	}
	
	
	/**
	 * Abfrage der Hintergrundfarbe 
	 */
	public String getBGColor() {
		if (colorBG.equals(Color.black))
		{
			return "black";
		}
		else if (colorBG.equals(Color.green))
		{
			return "green";
		}
		else if (colorBG.equals(Color.red))
		{
			return "red";
		}
		else if (colorBG.equals(Color.blue))
		{
			return "blue";
		}
		else if (colorBG.equals(Color.white))
		{
			return "white";
		}
		return "";
	}
	
	
	
	public void drawRectangle(Point upper_left, Point lower_right) {
		Image newDrawing = copyDrawing();
		Graphics g = newDrawing.getGraphics();
		g.setPaintMode();
		g.setColor(this.color);
		int  upper_leftx=(int) upper_left.getX();
		int upper_lefty=(int) upper_left.getY();
		int width=((int) lower_right.getX() - upper_leftx);
		int height=((int) lower_right.getY() - upper_lefty);
		
		g.drawRect(upper_leftx, upper_lefty, width,height);
		nextImage(newDrawing);
		
	}
	
	public void drawOval(Point upper_left, Point lower_right) {
		
		Image newDrawing = copyDrawing();
		Graphics g = newDrawing.getGraphics();
		g.setPaintMode();
		g.setColor(this.color);
		int  upper_leftx=(int) upper_left.getX();
		int upper_lefty=(int) upper_left.getY();
		int width=((int) lower_right.getX() - upper_leftx);
		int height=((int) lower_right.getY() - upper_lefty);
		
		g.drawOval(upper_leftx, upper_lefty, width,height);
		nextImage(newDrawing);
	}
	
	public void drawPolyLine(java.util.List<Point> points)
	{
		Image newDrawing = copyDrawing();
		Graphics g = newDrawing.getGraphics();
		if (points.size() > 1)
		{
			g.setColor(color);
			g.setPaintMode();
			int startx = (int) points.get(0).getX();
			int starty = (int) points.get(0).getY();
			int endx = (int) points.get(1).getX();
			int endy = (int) points.get(1).getY();
			g.drawLine(startx, starty, endx, endy);
			points.remove(0);
			nextImage(newDrawing);
			drawPolyLine(points);
		}
	}
	
	public Image getDrawing()
	{
        return drawing;
	}
	
	public void writeImage(Image img, String filename) throws IOException
	{
		//erzeugt PNG Datei des Bildes
		//ImageIO.write((RenderedImage) img,"png", new File(filename + ".png"));
		MyBMPFile.write(filename + ".bmp", img);
	}
	
	public Image readImage(String filename) throws IOException
	{
		//Für PNG Bilder
		//File file = new File(filename + ".png");
		//BufferedImage openDrawing = ImageIO.read(file);
		Image openDrawing = MyBMPFile.read(filename + ".bmp");
		return openDrawing;
	}
	
	
	public void clear()
	{
		drawing = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = drawing.getGraphics();
		g.setPaintMode();
		g.setColor(colorBG);
		g.fillRect(0, 0, getWidth(), getHeight());
		nextImage(drawing);
	}
	
	public void nextImage(Image newDrawing)
	{
		if (newDrawing != null)
		{
			Graphics g = drawing.getGraphics();
			g.drawImage(newDrawing, 0, 0, null);
		}
		Graphics g = panel.getGraphics();
		g.drawImage(newDrawing, 0, 0, null);
	}
	
	private Image copyDrawing()
	{
		Image new_drawing = createImage(getWidth(), getHeight());
		if (drawing != null)
		{
			Graphics g = new_drawing.getGraphics();
			g.drawImage(drawing, 0, 0, null);
		}
		return new_drawing;

	}
	
}
