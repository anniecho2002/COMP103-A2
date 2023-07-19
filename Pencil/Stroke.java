import ecs100.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

/**
 * Object containing the record of a draw action
 */
public class Stroke {
    private List<Double> x = new ArrayList<Double>();
    private List<Double> y = new ArrayList<Double>();
    private Color color;
    private double lineWidth;
    
    /**
     * Constructor
     */
    public Stroke(Color colorLine, double width){
        this.color = colorLine;
        this.lineWidth = width;
    }
    
    /**
     * Adds an x and y value that the stroke has
     */
    public void addPoint(double xVal, double yVal){
        x.add(xVal);
        y.add(yVal);
    }
    
    /**
     * Returns the color of the stroke
     */
    public Color getColor(){
        return color;
    }
    
    /**
     * Returns the line width of the stroke
     */
    public double getLineWidth(){
        return lineWidth;
    }
    
    /**
     * Redraws the line
     */
    public void redraw(){
        UI.setLineWidth(this.lineWidth);
        UI.setColor(color);
        for (int i=0; i<x.size()-1; i++){
            UI.drawLine(x.get(i), y.get(i), x.get(i+1), y.get(i+1));
        }
    }
}
