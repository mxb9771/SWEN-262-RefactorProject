package View;

import Control.ControlDeskEvent;
import Model.LaneEvent;
import Model.PinsetterEvent;

public interface Observer {

    void receiveEvent(LaneEvent laneEvent);

    void receiveEvent(PinsetterEvent pinsetterEvent);

    void receiveEvent(ControlDeskEvent controlDeskEvent);

}
