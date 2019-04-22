package View;

import Control.ControlDeskEvent;
import Control.LaneEvent;
import Control.PinsetterEvent;

public interface Observer {

    void receiveEvent(PinsetterEvent pe);

    void receiveEvent(ControlDeskEvent ce);

    void receiveEvent(LaneEvent le);

}
