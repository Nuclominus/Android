package com.nuclominus.offlinetwitterclient.Events;


public class OnlineEvent {
    private boolean network_state = false;

    public OnlineEvent(boolean network_state){
        this.network_state = network_state;
    }

    public boolean getState(){
        return network_state;
    }
}
