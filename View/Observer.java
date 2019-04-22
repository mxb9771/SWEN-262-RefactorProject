package View;

import Control.ControlDeskEvent;
import Model.LaneEvent;
import Model.PinsetterEvent;

public interface Observer {

    void receiveEvent(PinsetterEvent pe);

    void receiveEvent(ControlDeskEvent ce);

    void receiveEvent(LaneEvent le);

}
