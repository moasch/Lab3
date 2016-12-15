import java.beans.PropertyChangeListener;

/**
 * Created by Nils on 2016-12-15.
 */
public interface IObservable {

    public void addObserver(PropertyChangeListener observer);

    public void removeObserver(PropertyChangeListener observer);

}
