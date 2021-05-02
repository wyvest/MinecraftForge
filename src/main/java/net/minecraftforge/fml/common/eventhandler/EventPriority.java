package net.minecraftforge.fml.common.eventhandler;

/**
 * Priority of event listeners, listeners will be sorted with respect to this priority level.
 * <p>
 * Note:
 * Due to using a ArrayList in the ListenerList, these need to stay in a contiguous index starting at 0. {Default ordinal}
 */
public enum EventPriority implements IEventListener {
    HIGHEST, //First to execute
    HIGH,
    NORMAL,
    LOW,
    LOWEST; //Last to execute

    @Override
    public void invoke(Event event) {
        event.setPhase(this);
    }
}
