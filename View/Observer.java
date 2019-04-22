package View;

import Control.Events.ControlDeskEvent;
import Control.Events.LaneEvent;
import Control.Events.PinsetterEvent;

public interface Observer {

    void receiveEvent(PinsetterEvent pe);

    void receiveEvent(ControlDeskEvent ce);

    void receiveEvent(LaneEvent le);

}
