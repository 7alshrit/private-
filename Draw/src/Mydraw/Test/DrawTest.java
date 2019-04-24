package Mydraw.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import Mydraw.ColorException;
import Mydraw.Draw;
import Mydraw.DrawGUI;

public class DrawTest {
	
	private Draw draw;
	private DrawGUI gui;
	
	public DrawTest() {
		draw = new Draw();
		gui = draw.getWindow();
	}
	
	@Test
	public void	 testSetFGColor() 
	{
		boolean ex = false;
	 	try 
	 	{
			draw.getWindow().setFGColor("green");
		} catch (ColorException e) {
			ex = true;
		}
	 	assertEquals("green",gui.getFGColor()); 
	 	assertNotEquals("hallo", gui.getFGColor());
	 	assertFalse(ex);
	 	try {
	 		ex=false;
			gui.setFGColor("white");
		} catch (ColorException e) {
			// TODO Auto-generated catch block
			ex=true;
		}
	 	assertTrue(ex);
	 	
	}
	
	@Test
	public void	 testSetBGColor() 
	{
		boolean ex = false;
	 	try 
	 	{
			draw.getWindow().setBGColor("black");
		} catch (ColorException e) {
			ex = true;
		}
	 	assertEquals("black",gui.getBGColor()); 
	 	assertNotEquals("hallo", gui.getBGColor());
	 	assertFalse(ex);
	 	try {
	 		ex=false;
			gui.setBGColor("kfdjfk");
		} catch (ColorException e) {
			// TODO Auto-generated catch block
			ex=true;
		}
	 	assertTrue(ex);
	 	
	}
	@Test
	public void testSetPixel() {
		gui.setWidth(100);
		gui.setHeight(200);
		int width=gui.getWidth();
		int height =gui.getHeight();
		
		assertEquals(100,width);
		assertEquals(200,height);
		assertNotEquals(10,width);
		assertNotEquals(-200,height);
		gui.setHeight(1);
		height=gui.getHeight();
		assertEquals(1,height);
		//TODO
		
	}
	
	
	@Test
	public void testautodraw() 
	{
		boolean ex = false;
		boolean imgEqual;
		gui.autoDraw();
		BufferedImage auto = (BufferedImage) gui.getDrawing();
		Image referenz = null;
		try {
			referenz = gui.readImage("ReferenzBild");
		} catch (IOException e) {
			ex = true;
		}
		BufferedImage referenzBild = convertToBufferedImage(referenz);
		imgEqual = imageEqual(auto, referenzBild);		
		assertFalse(ex);
		assertTrue(imgEqual);
		
		auto.setRGB(0, 0, auto.getRGB(0, 0)+1);
		imgEqual = imageEqual(auto, referenzBild);
		assertFalse(imgEqual);
		
	}
	
	@Test
	public void testWriteRead() {
		boolean ex=false;
		boolean imgEqual;//=true;
		gui.autoDraw();
		BufferedImage gemaltesBild = (BufferedImage) gui.getDrawing();
		try {
			gui.writeImage(gui.getDrawing(),"gemaltesBild");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ex=true;
		}
		assertFalse(ex);
		try {
			gui.readImage("gemaltesBild");
			ex=false;
		} catch (IOException e) {
		
			ex=true;
		}
		BufferedImage gelesenesBild = (BufferedImage) gui.getDrawing();
		imgEqual= imageEqual(gemaltesBild,gelesenesBild );
		assertTrue(imgEqual);
		assertFalse(ex);
		
		try {
			gui.readImage("weruziuwehfkj");
			ex=false;
		} catch (IOException e) {
					
			ex=true;
		}
		assertTrue(ex);
		
		try {
			gui.writeImage(gui.getDrawing(),"C:\\Windows\\System32\\weruziuwehfkj");
			ex=false;
		} catch (IOException e) {
					
			ex=true;
		}
		assertTrue(ex);
		
	}
	
	
	
	
	
	
	private static BufferedImage convertToBufferedImage(Image image)
	{
	    BufferedImage newImage = new BufferedImage(
	        image.getWidth(null), image.getHeight(null),
	        BufferedImage.TYPE_INT_ARGB);
	    Graphics g = newImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    return newImage;
	}
	
	private static boolean imageEqual(BufferedImage a, BufferedImage b)
	{
		if (a.getWidth()==b.getWidth() && a.getHeight()==b.getHeight())
		{
			for (int i=0; i < a.getWidth()-1; i++)
			{
				for (int j=0; j < a.getHeight()-1; j++)
				{
					if (a.getRGB(i, j) != b.getRGB(i, j))
					{
						return false;
					}
				}
			}
		}
		else
		{
			return false;
		}
		return true;
	}


}
