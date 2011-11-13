package com.schneenet.android.lasers.levels;

import java.io.IOException;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;

import com.schneenet.android.lasers.obj.GameObjectRenderable;
import com.schneenet.android.lasers.obj.LightSource;
import com.schneenet.android.lasers.obj.LightTarget;
import com.schneenet.android.lasers.obj.Mirror;
import com.schneenet.android.lasers.obj.Targetable;
import com.schneenet.android.lasers.obj.Wall;

public class LevelFactory {

	public static class AsyncLevelLoader extends AsyncTask<Void, Integer, LaserLightPuzzleLevel> {

		public AsyncLevelLoader(InputSource is, AsyncLoaderListener l) {
			mInputSource = is;
			mListener = l;
		}
		
		@Override
		protected LaserLightPuzzleLevel doInBackground(Void... args) {
			LevelParser parser = new LevelParser(new LevelParser.ParserListener() {
				@Override
				public void onError(String msg, Exception e) {
					errString = msg;
					errException = e;
					publishProgress(-1);
				}
			});
			
			return parser.loadLevel(mInputSource);
		}
		
		protected void onProgressUpdate(Integer... progress) {
			if (progress.length > 0 && progress[0] == -1) {
				mListener.onError(errString, errException);
			}
		}
		
		protected void onPostExecute(LaserLightPuzzleLevel result) {
			mListener.onLevelLoaded(result);
		}
		
		private String errString;
		private Exception errException;
		
		private InputSource mInputSource;
		private AsyncLoaderListener mListener;
		
		public interface AsyncLoaderListener {
			public void onError(String message, Exception e);
			public void onLevelLoaded(LaserLightPuzzleLevel level);
		}
		
	}
	
	public static class LevelParser extends DefaultHandler {

		public LevelParser(ParserListener l) {
			if (l == null)
				throw new IllegalArgumentException("l (FactoryListener) can not be NULL!");
			mListener = l;
			tagStack = new Stack<String>();
			charsBuilder = new StringBuilder();
		}

		public LaserLightPuzzleLevel loadLevel(InputSource is) {
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				xr.setContentHandler(this);
				xr.parse(is);
				return theLevel;
			} catch (SAXException e) {
				mListener.onError(e.getLocalizedMessage(), e);
			} catch (IOException e) {
				mListener.onError(e.getLocalizedMessage(), e);
			} catch (ParserConfigurationException e) {
				mListener.onError(e.getLocalizedMessage(), e);
			}
			return null;
		}

		@Override
		public void characters(char[] chs, int start, int length) throws SAXException {
			charsBuilder.append(chs, start, length);
		}

		@Override
		public void endDocument() throws SAXException {
			if (!tagStack.empty()) {
				throw new SAXException("Parse Error: Reached end of document without end tag for: " + tagStack.pop());
			}
		}

		@Override
		public void endElement(String uri, String lName, String qName) throws SAXException {
			if (qName != null && !tagStack.empty()) {
				String popTag = tagStack.pop();
				if (!qName.equals(popTag)) {
					throw new SAXException("Parse Error: End tag mismatch! (looking for \"" + popTag + "\" but got \"" + qName + "\")");
				} else {
					if (TAG_LEVEL.equals(qName)) {
						// Finished with level
						inLevel = false;
					} else if (TAG_NAME.equals(qName)) {
						// Finished with name
						theLevel.name = charsBuilder.toString().trim();
						charsBuilder = new StringBuilder();
					} else if (TAG_AUTHOR.equals(qName)) {
						theLevel.author = charsBuilder.toString().trim();
						charsBuilder = new StringBuilder();
					} else if (TAG_DIFFICULTY.equals(qName)) {
						// Finished with difficulty
						theLevel.difficulty = Integer.parseInt(charsBuilder.toString().trim());
						charsBuilder = new StringBuilder();
					} else if (Data.TAG_LIGHTSOURCE.equals(qName) || Data.TAG_LIGHTTARGET.equals(qName) || Data.TAG_MIRROR.equals(qName) || Data.TAG_WALL.equals(qName)) {
						if (curObject != null) {
							theLevel.gameObjects.add(curObject);
							curObject = null;
						} else {
							throw new SAXException("Parse Error: End tag found with missing starting tag: \"" + qName + "\"");
						}
					}
				}
			} else {
				throw new SAXException("Parse Error: End tag mismatch! Extra end tag: \"" + qName + "\"");
			}
		}

		@Override
		public void startDocument() throws SAXException {
			// Symbolically start the level
			theLevel = new LaserLightPuzzleLevel();
		}

		@Override
		public void startElement(String uri, String lName, String qName, Attributes as) throws SAXException {
			if (TAG_LEVEL.equals(qName)) {
				if (!inLevel) {
					inLevel = true;
				} else {
					throw new SAXException("Parse Error: Only one \"level\" tag allowed per level file!");
				}
			} else if (TAG_NAME.equals(qName) || TAG_DIFFICULTY.equals(qName) || TAG_DATA.equals(qName)) {
				// Don't care here
			} else if (Data.TAG_LIGHTSOURCE.equals(qName)) {
				if (as.getIndex("x") != -1 && as.getIndex("y") != -1 && as.getIndex("rot") != -1 && as.getIndex("color") != -1) {
					float x = Float.parseFloat(as.getValue("x"));
					float y = Float.parseFloat(as.getValue("y"));
					boolean moveable = as.getIndex("moveable") != -1 ? Boolean.parseBoolean(as.getValue("moveable")) : false;
					boolean rotatable = as.getIndex("rotatable") != -1 ? Boolean.parseBoolean(as.getValue("rotatable")) : true;
					float initialRotation = Float.parseFloat(as.getValue("rot"));
					int color = Color.parseColor(as.getValue("color"));
					curObject = new LightSource(x, y, moveable, rotatable, initialRotation, color);
				} else {
					throw new SAXException("Parse Error: Required attributes missing for tag \"" + qName + "\".");
				}
			} else if (Data.TAG_LIGHTTARGET.equals(qName)) {
				if (as.getIndex("x") != -1 && as.getIndex("y") != -1 && as.getIndex("size") != -1) {
					float x = Float.parseFloat(as.getValue("x"));
					float y = Float.parseFloat(as.getValue("y"));
					float size = Float.parseFloat(as.getValue("size"));
					curObject = new LightTarget(x, y, size);
				} else {
					throw new SAXException("Parse Error: Required attributes missing for tag \"" + qName + "\".");
				}
			} else if (Data.TAG_MIRROR.equals(qName)) {
				if (as.getIndex("x") != -1 && as.getIndex("y") != -1 && as.getIndex("rot") != -1 && as.getIndex("size") != -1) {
					float x = Float.parseFloat(as.getValue("x"));
					float y = Float.parseFloat(as.getValue("y"));
					boolean moveable = as.getIndex("moveable") != -1 ? Boolean.parseBoolean(as.getValue("moveable")) : false;
					boolean rotatable = as.getIndex("rotatable") != -1 ? Boolean.parseBoolean(as.getValue("rotatable")) : true;
					float initialRotation = Float.parseFloat(as.getValue("rot"));
					float length = Float.parseFloat(as.getValue("size"));
					curObject = new Mirror(x, y, moveable, rotatable, initialRotation, length);
				} else {
					throw new SAXException("Parse Error: Required attributes missing for tag \"" + qName + "\".");
				}
			} else if (Data.TAG_WALL.equals(qName)) {
				if (as.getIndex("x1") != -1 && as.getIndex("x2") != -1 && as.getIndex("y1") != -1 && as.getIndex("y2") != -1) {
					float x1 = Float.parseFloat(as.getValue("x1"));
					float y1 = Float.parseFloat(as.getValue("y1"));
					float x2 = Float.parseFloat(as.getValue("x2"));
					float y2 = Float.parseFloat(as.getValue("y2"));
					curObject = new Wall(new PointF(x1, y1), new PointF(x2, y2));
				} else {
					throw new SAXException("Parse Error: Required attributes missing for tag \"" + qName + "\".");
				}
			} else if (TAG_REQUIRES.equals(qName)) {
				if (curObject != null && curObject instanceof Targetable) {
					if (as.getIndex("color") != -1) {
						int color = Color.parseColor(as.getValue("color"));
						((Targetable) curObject).setRequiresColor(true, color);
					} else {
						throw new SAXException("Parse Error: Required attributes missing for tag \"" + qName + "\".");
					}
				} else {
					throw new SAXException("Parse Error: Tag \"" + qName + "\" found without valid parent.");
				}
			} else {
				throw new SAXException("Parse Error: Unknown tag: \"" + qName + "\"");
			}

			// Push the element on to the stack
			tagStack.push(qName);
		}

		private ParserListener mListener;
		private Stack<String> tagStack;
		private LaserLightPuzzleLevel theLevel;

		private StringBuilder charsBuilder;
		private GameObjectRenderable curObject;

		private boolean inLevel = false;

		public static final String TAG_LEVEL = "level";
		public static final String TAG_NAME = "name";
		public static final String TAG_AUTHOR = "author";
		public static final String TAG_DIFFICULTY = "difficulty";
		public static final String TAG_DATA = "data";

		public static final String TAG_REQUIRES = "requires";

		public static class Data {
			public static final String TAG_WALL = "wall";
			public static final String TAG_MIRROR = "mirror";
			public static final String TAG_LIGHTSOURCE = "laser";
			public static final String TAG_LIGHTTARGET = "target";
		}

		public interface ParserListener {
			public void onError(String msg, Exception e);
		}
	}
}
