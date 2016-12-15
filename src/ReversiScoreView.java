import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by Nils on 2016-12-15.
 */
public class ReversiScoreView implements PropertyChangeListener{

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getSource().getClass() == ReversiModel.class && evt.getPropertyName().equals("ScoreUpdate")){
            ReversiModel model = (ReversiModel) evt.getSource();
            System.out.println("White score: " + model.getWhiteScore());
            System.out.println("Black score: " + model.getBlackScore());
            System.out.println("Player turn: " + model.getTurnColor());
        }
    }

}
