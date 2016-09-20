package model.Model;

import java.util.Objects;

/**
 *
 * @author susanti_2
 */
public class AspectSentiment {
    private String aspect;
    private String sentiment;
    private int orientation; //pos = 1, neg = -1, netral = 0

    public AspectSentiment(String aspect, String sentiment, int orientation) {
        this.aspect = aspect;
        this.sentiment = sentiment;
        this.orientation = orientation;
    }

    public void print() {
        if (isPositive()) {
            System.out.print("(+) ");
        } else if (isNegative()) {
            System.out.print("(-) ");
        }
        System.out.println( aspect + " => " + sentiment);
    }

    public String toString() {
        String output="";
        if (isPositive()) {
            output+="(+) ";
        } else if (isNegative()) {
            output+="(-) ";
        }
        output+= aspect + " => " + sentiment;
        return output;
    }

    public String getAspect() {
        return aspect;
    }

    public String getSentiment() {
        return sentiment;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setAspect(String aspect) {
        this.aspect = aspect;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public void setorientation(int orientation) {
        this.orientation = orientation;
    }

    public void setPosOrientation() {
        this.orientation = 1;
    }

    public void setNegOrientation() {
        this.orientation = -1;
    }

    public boolean isPositive(){
        return orientation == 1;
    }

    public boolean isNegative(){
        return orientation == -1;
    }

    public boolean isNetral(){
        return orientation == 0;
    }

    @Override
    public boolean equals(Object ob) {
        if (ob instanceof AspectSentiment) {
            AspectSentiment as = (AspectSentiment) ob;
            if (aspect.equals(as.aspect)
                    && sentiment.equals(as.sentiment)
                    && orientation==as.orientation)
                return true;
        }
        return false;
   }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.aspect);
        hash = 11 * hash + Objects.hashCode(this.sentiment);
        hash = 11 * hash + this.orientation;
        return hash;
    }
}
