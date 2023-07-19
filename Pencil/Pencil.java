/* Code for COMP103 - 2021T2, Assignment 2
 * Name: Annie Cho
 * Username: choanni
 * ID: 300575457
 */

// clear redo every time a new stroke is drawn
import ecs100.*;
import java.util.*;
import java.awt.Color;
import javax.swing.JColorChooser;

/** Pencil   */
public class Pencil{
    private double lastX;
    private double lastY;
    private Stack<Stroke> history = new Stack<Stroke>();
    private Stack<Stroke> redo = new Stack<Stroke>();
    
    private Color color = Color.black;
    private double lineWidth = 1;

    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);
        UI.addSlider("Line Width:", 1, 20, 1, this::setLineWidth);
        UI.addButton("Line Color", this::setColor);
        UI.addButton("Undo", this::undo);
        UI.addButton("Redo", this::redo);
        UI.addButton("Quit", UI::quit);
        UI.setLineWidth(3);
        UI.setDivider(0.0);
    }

    /**
     * Respond to mouse events
     */
    public void doMouse(String action, double x, double y) {
        if (action.equals("pressed")){
            history.push(new Stroke(color, lineWidth));
            lastX = x;
            lastY = y;
            redo.clear();
        }
        else if (action.equals("dragged")){
            UI.drawLine(lastX, lastY, x, y);
            lastX = x;
            lastY = y;
            history.peek().addPoint(x,y);
        }
        else if (action.equals("released")){
            UI.drawLine(lastX, lastY, x, y);
        }
    }
    
    /**
     * Sets the line width of the pen
     */
    public void setLineWidth(double userWidth){
        lineWidth = userWidth;
        UI.setLineWidth(lineWidth);
    }
    /**
     * Sets the color of the pen
     */
    public void setColor(){
        color = JColorChooser.showDialog(null, "FillColor", Color.white);
        UI.setColor(color);
    }
    
    /**
     * Undoes the drawing function
     */
    public void undo(){
        if (!history.isEmpty()){
            color = history.peek().getColor();
            lineWidth = history.peek().getLineWidth();
            Stroke lastStroke = history.pop();
            redo.add(lastStroke);
            UI.setColor(color);
            UI.setLineWidth(lineWidth);
            redraw();
        }
    }
    
    /**
     * Redoes the actions that have been undone
     */
    public void redo(){
        if (!redo.isEmpty()){
            Stroke lastStroke = redo.pop();
            history.add(lastStroke);
            redraw();
            UI.setColor(lastStroke.getColor());
            UI.setLineWidth(lastStroke.getLineWidth());
        }
    }
    
    /**
     * Clears then redraws everything on screen again
     */
    public void redraw(){
        UI.clearGraphics();
        if(!history.isEmpty()){
            for (Stroke s: history){
                s.redraw();
            }
        }
    }

    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }
}
