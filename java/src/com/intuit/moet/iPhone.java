package com.intuit.moet;import java.awt.Rectangle;import java.io.BufferedReader;import java.io.File;import java.io.IOException;import java.io.InputStreamReader;import java.net.InetSocketAddress;import java.net.Proxy;import java.net.Proxy.Type;import java.net.SocketAddress;import java.net.URL;import java.net.URLConnection;import java.text.DateFormat;import java.text.SimpleDateFormat;import java.util.Calendar;import java.util.logging.FileHandler;import java.util.logging.Handler;import java.util.logging.Logger;import java.util.logging.SimpleFormatter;import javax.imageio.ImageIO;import org.sikuli.script.App;import org.sikuli.script.FindFailed;import org.sikuli.script.ImageLocator;import org.sikuli.script.Location;import org.sikuli.script.Match;import org.sikuli.script.Region;import org.sikuli.script.Screen;import org.sikuli.script.ScreenImage;/** * This class consists of library methods for iPhone emulator. * It utilizes Sikuli open source library, developed by MIT. *  * @author eong * */public class iPhone implements IDevice{	/**	 * Location of where all images are stored.	 */	public static final String IMAGES = "resources" + File.separator + "iphone";		public static final int ADJUSTX = 23;	public static final int ADJUSTY = 136;			/** 	 * List of images to click on.	 */	public static final String ButtonHome = "homebutton.png";	public static final String ButtonOK = "buttonok.png";	public static final String KeyBackspace = "keybackspace.png";	public static final String KeyCancel = "keycancel.png";	public static final String KeyDone = "keydone.png";	public static final String KeyGo = "keygo.png";	public static final String KeyNext = "keynext.png";	public static final String KeyReturn = "keyreturn.png";	public static final String KeySearch = "keysearch.png";	public static final String DialDivider = "dialdivider.png";	public static final String DialLeftDivider = "dialleftdivider.png";	public static final String DialRightDivider = "dialrightdivider.png";	public static final String DialFocus = "dialfocus.png";			public static App app = null;	public Settings settings;	public static Screen screen = null;	public static Region region = null;	protected Location loc = new Location(0, 0);	public int x;	public int y;		private static final Logger LOG = Logger.getLogger(Settings.class.getName());	private static Logger httplogger = null;	private static Handler handler; 	private static final String charlesUrl = "http://control.charles:80/";	private static String httplogfile = null;	private static StringBuffer charlesLog = new StringBuffer();	private static final SocketAddress socket = new InetSocketAddress("127.0.0.1", 8888);	private static final Proxy proxy = new Proxy(Type.HTTP, socket);		/**	 * Recognizes iPhone simulator and instantiates screen, region and app.	 * @param settings Settings include resolution, output directories etc.	 */	public iPhone(Settings settings) 	{		try		{			ImageLocator.addImagePath(IMAGES);			ImageLocator.addImagePath(settings.expectedDir);			ImageLocator.addImagePath(settings.expectedDir + ".." + File.separator);			LOG.info("Image paths - " + IMAGES + ", " + settings.expectedDir + ", " 					+ settings.expectedDir + ".." + File.separator);						this.settings = settings;			app = new App(settings.iphoneExec);			screen = new Screen();			// Note. This assumes there is a start-up script to start simulator			// so that each test does not have to wait for simulator to start and			// verify the sanity of the simulator.			screen.setRect(app.window());			region = new Region(screen.getRect());				this.x = region.getX();			this.y = region.getY();						// Set up charles logger			//System.setProperty("http.proxyHost", "127.0.0.1");			//System.setProperty("http.proxyPort", "8888");			httplogfile = this.settings.actualDir + "device.log";			if (httplogger == null)			{				httplogger = Logger.getLogger(iPhone.class.getName());			}			httplogger.setUseParentHandlers(false);			this.resetLogFileHandler();		}		catch (Exception e)		{			e.printStackTrace();		}	};		/**	 * This is to reset log file handler with every test as	 * they are moved to a different directory after each test completion.	 * Log file is charles.log (iPhone) and adb.log (Android).	 * @throws IOException file not found exception	 */	public void resetLogFileHandler() throws IOException	{		// if recording status is stop, start recording		if (charlesLog.length() == 0)		{			URL url = new URL(charlesUrl + "recording/start");		    URLConnection conn = url.openConnection(proxy);			conn.getContentType();			conn = null;			this.clearLog();		}		if (handler != null)		{			httplogger.removeHandler(handler);			handler.close();			handler = null;		}	    handler = new FileHandler(httplogfile, true);	    handler.setFormatter(new SimpleFormatter());		httplogger.addHandler(handler);	}		/**	 * Launch app in $outputDir/resources	 * @param app App name to launch. 	 *        Application should be placed in $outputDir/resources directory.	 */	public void launch(String app) throws Exception	{		this.toggleAnimation();		String appLocation = this.settings.outputDir + File.separator 			+ "resources" + File.separator + "builds" + File.separator + app;		String [] commandArray =  { "iphonesim", "launch", appLocation };		File findApp = new File(appLocation);		if (findApp.isDirectory())		{			LOG.info("Launching app - " + commandArray[0] + " " + commandArray[1] + " " + commandArray[2]);			Runtime.getRuntime().exec(commandArray);			}		else			throw new IOException();	}		/**	 * Not required for iPhone, simply relaunch app to terminate last application.	 * @param packageName 	 */	public void terminate(String notUsed) throws Exception	{			//String killCommand = "kill `ps acx|grep -i iphonesim | awk {'print $1'}`";	}		/**	 * NOT applicable for iPhone.	 */	public void menu() { return; };			/**	 * Focus device on application	 */	public void focus() 	{ 		app.focus(); 	};			/**	 * Presses the home button key.	 */	public void home() 	{		screen.keyDown(Key.SHIFT);		screen.keyDown(Key.META);		screen.keyDown("h");		screen.keyUp(Key.META);		screen.keyUp(Key.SHIFT);		screen.keyUp("h");	};		/**	 * Calls home method to press Home button.	 */	public void back() 	{		this.home();	};		/**	 * Enter backspaces to delete text.	 * @param num number of backspaces	 */	public void backspaces(int num) 	{		while (num > 0)		{			screen.keyDown(Key.BACKSPACE);			screen.keyUp(Key.BACKSPACE);			num--;		}	};		/**	 * Clicks on enter/return on virtual keyboard.	 */ 	public void enter() 	{		try		{			app.focus();			region.click(KeyReturn, 0);		}		catch (FindFailed e)		{			e.printStackTrace();		}			};		/**	 * Enters inputStr into focused field.	 * @param inputStr input string to enter	 */	public void enter(String inputStr)	{		try		{			screen.type(null, inputStr, 0);		}		catch (FindFailed e)		{			e.printStackTrace();		}	};	/**	 * Scroll "up"/"down"/"left"/"right" once.	 * @param direction  "up"/"down"/"left"/"right"	 */	public void scroll(String direction)	{		this.scroll(direction, 1);	}		/**	 * Scroll "up"/"down"/"left"/"right" multiple number of times.	 * @param direction  "up"/"down"/"left"/"right"	 * @param num number of times to scroll	 */	public void scroll(String direction, int num)	{		// default direction is "down"	    int xStart = 50;	    int xEnd = 20;	    int yStart = 430;	    int yEnd = 200;	    if (direction == "up")	    {	        yStart = 200;	        yEnd = 350;	    }	    else if (direction == "right")	    {	        xStart = 220;	        yEnd = yStart;	    }	    else if (direction == "left")	    {	        xEnd = 330;	        yEnd = yStart;	    }	    while (num > 0)	    {	        num--;	        this.drag(xStart, yStart, xEnd, yEnd);	    }	}		/**	 * Touch on screen at co-ordinates (x, y).	 * @param x x co-ordinate	 * @param y y co-ordinate	 */	public void touch(int x, int y)	{		try		{			x += this.x;			y += this.y;			loc.setLocation(x, y);			app.focus();			region.click(loc, 0);		}		catch(FindFailed e)		{			e.printStackTrace();		}	}		/**	 * Touch on screen at co-ordinates (x, y).	 * @param x x co-ordinate	 * @param y y co-ordinate	 */	public void touchActual(int x, int y)	{		Location loc = new Location(x, y);		try		{			loc.setLocation(x, y);			app.focus();			region.click(loc, 0);		}		catch(FindFailed e)		{			e.printStackTrace();		}	}		/**	 * Touch on screen at co-ordinates ("x%", "y%') of current resolution.	 * @param x x% e.g. "10%" of max horizontal resolution	 * @param y y% e.g. "20%" of max vertical resolution	 */	public void touch(String x, String y)	{		int xInt = Util.absCoordinates(x, "x", this.settings);		int yInt = Util.absCoordinates(y, "y", this.settings);		this.touch(xInt, yInt);	};		/**	 * Drag on screen from co-ordinates (fromX, fromY) to (toX, toY).	 * @param fromX start x co-ordinate	 * @param fromY start y co-ordinate	 * @param toX end x co-ordinate	 * @param toY end y co-ordinate	 */	public void dragActual(int fromX, int fromY, int toX, int toY)	{		try		{			loc.setLocation(fromX, fromY);			Location toLoc = new Location(toX, toY);			app.focus();			region.dragDrop(loc, toLoc, 0);		}		catch (FindFailed e)		{			e.printStackTrace();		}	};		/** Returns Sikuli Region **/	public Region getRegion()	{		return region;	}			public Calendar getCurrentDate(String imageToFocus)	{		try 		{			// Get current date			Match match = iPhone.region.find(imageToFocus);			Region dateRegion = new Region(match);			dateRegion.setX(match.x + 150);			String currentDate = dateRegion.text().trim();			currentDate = currentDate.replaceAll(" ", "");			currentDate = currentDate.replaceAll("O", "0");			currentDate = currentDate.substring(0,8);			System.out.println("Current date is " + currentDate);			DateFormat currentFormat = new SimpleDateFormat("MM/dd/yy");			Calendar currentCal = Calendar.getInstance();			currentCal.setTime(currentFormat.parse(currentDate));			return currentCal;		}		catch(Exception e)		{			return null;		}	}		/**	 * Drag on screen from co-ordinates (fromX, fromY) to (toX, toY).	 * @param fromX start x co-ordinate	 * @param fromY start y co-ordinate	 * @param toX end x co-ordinate	 * @param toY end y co-ordinate	 */	public void drag(int fromX, int fromY, int toX, int toY)	{		try		{			fromX += this.x;			fromY += this.y;			loc.setLocation(fromX, fromY);			toX += this.x;			toY += this.y;			Location toLoc = new Location(toX, toY);			app.focus();			region.dragDrop(loc, toLoc, 0);		}		catch (FindFailed e)		{			e.printStackTrace();		}	};		/**	 * Drag on screen from co-ordinates (fromX, fromY) to (toX, toY).	 * @param fromX start x% e.g. "10%" of max horizontal resolution	 * @param fromY start y% e.g. "10%" of max vertical resolution	 * @param toX end x% e.g. "10%" of max horizontal resolution	 * @param toY end y% e.g. "10%" of max vertical resolution	 */	public void drag(String fromX, String fromY, String toX, String toY)	{		int fromXInt = Util.absCoordinates(fromX, "x", this.settings);		int fromYInt = Util.absCoordinates(fromY, "y", this.settings);		int toXInt = Util.absCoordinates(toX, "x", this.settings);		int toYInt = Util.absCoordinates(toY, "y", this.settings);		this.drag(fromXInt, fromYInt, toXInt, toYInt);	};			/**	 * Touch image on the screen.	 * @param image image file name	 */	public void touchImage(String image)	{		if (image == null)			return;		try		{			if (image.contains(".png"))				image = image.toLowerCase();			LOG.info("Pressing key " + image);			app.focus();			region.click(image, 0);		}		catch (FindFailed e)		{			e.printStackTrace();		}	}		/**	 * Takes screenshot of just the app (trim off the iPhone) 	 * and save to filename.	 * @param filename full path to save image to	 */	public void screenshot(String filename)	{			try		{			Rectangle rectangle = region.getRect();			rectangle.setBounds(					this.x + rectangle.x + ADJUSTX, 					this.y + rectangle.y + ADJUSTY, 					322, 					465);			app.focus();			ScreenImage image = screen.capture(rectangle);			String fullpath = this.settings.actualDir + filename;			if (!filename.contains(".png"))				fullpath = fullpath + ".png";			File outputfile = new File(fullpath);			ImageIO.write(image.getImage(), "png", outputfile);		}		catch (IOException e)		{			e.printStackTrace();		}	}		/**	 * Returns text content on current device (aka OCR).	 */	public String getText()	{		app.focus();		String results = region.text();		//LOG.info("Device text is " + results);		return results;	}		/**	 * Returns text content on current device (aka OCR) all lower case and trimmed.	 * @param removeSpaces this will remove spaces from the screen text in addition	 *        to lowercase and trimmed. 	 */	public String getText(boolean removeSpaces)	{		String text = this.getText();		if (text != null)			return text.toLowerCase().replaceAll(" ","");		else			return text;	}		/**	 * Set the picker scroller at column down or up (negative scroll).	 * @param column column to scroll	 * @param scroll number of scrolls, positive for scrolling down	 */	public void pickerScroll(int column, int scroll)	{		// set to and from X co-ordinates		if (scroll == 0) return;		int x = 0;				try 		{			app.focus();			Match leftDiv = region.find(DialLeftDivider);			switch (column)			{				case 1:					x = leftDiv.x + 20;					break;				case 2:					int [] index = new int [2];					ImageKit.findAll(this, DialDivider, index, true);					if (index.length > 1)						x = index[0] + 30 + this.x;					else						return;					break;				case 3 :					x = region.find(DialRightDivider).x - 5;					break;					}			int y = 45;			int startY = leftDiv.y + 100;						int endY = startY;						// set to and from Y co-ordinates			// if scroll is +, drag up to scroll down. Else, drag down to scroll up			while (scroll >= 9)			{				scroll = scroll - 8;				endY = startY - (y * 8);				this.dragActual(x, startY, x, endY);				Thread.sleep(500);			}			if (scroll < 0)			{				y = 40;				startY = startY - 90;				endY = startY - (y * scroll) + 5;								while (scroll <= -5)				{					endY = startY - (y * -4) + 5;					scroll = scroll + 4;					this.dragActual(x, startY, x, endY);					Thread.sleep(500);				}			}			if (scroll != 0)			{				endY = startY - (y * scroll) + 5;				this.dragActual(x, startY, x, endY);			}		}		catch (FindFailed e)		{			e.printStackTrace();		}		catch(InterruptedException e)		{			e.printStackTrace();		}	}		public void toggleAnimation()	{		screen.keyDown(Key.SHIFT);		screen.keyUp(Key.SHIFT);			screen.keyDown(Key.SHIFT);		screen.keyUp(Key.SHIFT);				screen.keyDown(Key.SHIFT);		screen.keyUp(Key.SHIFT);	}		/**	 * Returns env settings.	 */	public Settings getSettings()	{		return this.settings;	}			/** 	 * Retrieves log.	 * @return string from application logs	 */	public String getLog() 	{ 		if (!this.settings.log.contains("on"))			return null;				// Optimize performance by reusing the current log until saveLog is called		if (charlesLog.length() > 0)			return charlesLog.toString();		try		{			URL url = new URL(charlesUrl + "session/export-xml");		    URLConnection conn = url.openConnection(proxy);		    BufferedReader in = new BufferedReader(		                        new InputStreamReader(		                        conn.getInputStream()));		    String inputLine;		    while ((inputLine = in.readLine()) != null) 		    {			    if (inputLine.contains("?xml"))		    	{		    		// pretty print xml		    		String[] splitLine = inputLine.split("><");		    		int len = splitLine.length;		    		int i = 0;		    		charlesLog.append(splitLine[0]);		    		i++;		    		while (i < len)		    		{		    			charlesLog.append(">");		    			charlesLog.append("\n   <");		    			charlesLog.append(splitLine[i]);		    			i++;		    		}		    		charlesLog.append("\n\n");		    	}		    }		    in.close();			conn = null;		    return charlesLog.toString();		}		catch (IOException e)		{			return null;		}	}		/** 	 * Flushes log after saving it to logger.	 */	public void saveLog() 	{		httplogger.info(this.getLog());		this.clearLog();	}		/** 	 * Clear log.	 */	public void clearLog() 	{		if (!this.settings.log.contains("on"))			return;		try		{			URL url = new URL(charlesUrl + "session/clear");			URLConnection conn = url.openConnection(proxy);			conn.getContentType();			conn = null;			charlesLog.setLength(0);		}		catch (Exception e)		{			return;		}	}}