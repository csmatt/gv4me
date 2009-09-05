/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gvME;

import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author matt
 */
public class verticalScrollBox extends Canvas {

	static final String VOID_STRING = "";
	static final char SPACE_CHAR = ' ';
	 
	int width = 0;
	int height = 0;
	int innerWidth = 0;
	int innerHeight = 0;
	 
	int currentY = 0;
	int textHeight = 0;

    String text = null;
	 
	String[] textRows = null;
    
	static final int SCROLL_STEP = 25;
	 
	int scrollbarWidth = 4;
	int scrollbarHeight = 0;
	int scrollbarTop = 0;
	int scrollbarColor = 0x0000ff;
	 
	int borderWidth = 1;
	int borderColor = 0x000000;
	int bgColor = 0xffffff;
	 
	Font textFont = Font.getDefaultFont();
	int textColor = 0x000000;
	 
	int padding = 1;
	int interline = 2;
    
	public verticalScrollBox(String text)
	{
    this.text = text;
	this.width = getWidth();
	this.height = getHeight() - 100;
	 
	this.innerWidth = width - 2 * borderWidth - 2 * padding - scrollbarWidth;
	this.innerHeight = height - 2 * borderWidth - 2 * padding;

        setText();
	}
    
	public void setText()
	{
		this.textRows = getTextRows(text, textFont, innerWidth);
	 
		this.textHeight = textRows.length * (interline + textFont.getHeight());
	 
		scrollbarHeight = Math.min(innerHeight, innerHeight * innerHeight / textHeight);
	 
		scrollbarTop = 0;
	 
		currentY = 0;
	}
    
    public void scrollDown()
	{
		scroll(SCROLL_STEP);
	}
	public void scrollUp()
	{
		scroll(- SCROLL_STEP);	
	}
	private void scroll(int delta)
	{
		currentY += delta;
	 
		if(currentY < 0)
		{
			currentY = 0;
		}
		else if(currentY > textHeight - innerHeight)
		{
			currentY = Math.max(0, textHeight - innerHeight);
		}
	 
		scrollbarTop = innerHeight * currentY / textHeight;
	}

    public static String[] getTextRows(String text, Font font, int width) {
		char spaceChar = ' ';

		//will contain text rows
		Vector rowsVector = new Vector();

		//will contain current row text
		StringBuffer currentRowText = new StringBuffer();

		//indexes used to split text words
		int prevIndex = 0;
		int currIndex = text.indexOf(spaceChar);

		if(currIndex == -1)
		    	currIndex = text.length();

		//will hold widths of current row and token
		int rowWidth = 0;
		int tokenWidth = 0;

		//width of a single whitespace
		int whitespaceWidth = font.stringWidth(" ");

		//current text token
		String currentToken = null;

		while (currIndex != -1) {
			//get the current token
			currentToken = text.substring(prevIndex, currIndex);

			//get the width of current token..
			tokenWidth = font.stringWidth(currentToken);

			//..and update row width
			rowWidth += tokenWidth;

			//if row is not empty, add the whitespace width too
			if (currentRowText.length() > 0) {
				rowWidth += whitespaceWidth;
			}

			//if new row width is bigger than max width, and previous row is not empty
			if (currentRowText.length() > 0 && rowWidth > width) {
				//add current row text to rows Vector
				rowsVector.addElement(currentRowText.toString());

				//reinitialize current row with current token
				currentRowText.setLength(0);
				currentRowText.append(currentToken);

				//and update current row width
				rowWidth = tokenWidth;
			} else {
				//if current row is not empty, add a whitespace
				if (currentRowText.length() > 0)
					currentRowText.append(spaceChar);

				//and then add current token
				currentRowText.append(currentToken);
			}

			//check if text is ended
			if (currIndex == text.length())
				break;

			//update indexes
			prevIndex = currIndex + 1;

			currIndex = text.indexOf(spaceChar, prevIndex);

			if (currIndex == -1)
				currIndex = text.length();
		}

		//finally append current row, if not empty
		if (currentRowText.length() > 0) {
			rowsVector.addElement(currentRowText.toString());
		}

		//Convert our rows vector to a String array
		String[] rowsArray = new String[rowsVector.size()];

		rowsVector.copyInto(rowsArray);

		return rowsArray;
	}

    public void paint(Graphics g)
    {
        g.setColor(borderColor);
        g.fillRect(0, 0, width, height);

        g.setColor(bgColor);
        g.fillRect(borderWidth, borderWidth, width - 2 * borderWidth, height - 2 * borderWidth);

        g.setColor(textColor);
        g.setFont(textFont);

        g.translate(borderWidth + padding, borderWidth + padding);

        g.setClip(0, 0, innerWidth, innerHeight);

        if(textRows != null)
        {
            for(int i = 0; i < textRows.length; i++)
            {
                g.drawString(textRows[i], 0, i * (textFont.getHeight() + interline) - currentY, Graphics.TOP | Graphics.LEFT);
            }
        }

        g.setClip(0, 0, width, height);

        g.setColor(scrollbarColor);
        g.fillRect(innerWidth, scrollbarTop, scrollbarWidth, scrollbarHeight);

        g.translate(- (borderWidth + padding), - (borderWidth + padding));
    }
}
