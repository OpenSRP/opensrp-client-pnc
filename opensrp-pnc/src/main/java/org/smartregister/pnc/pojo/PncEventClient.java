package org.smartregister.pnc.pojo;


import androidx.annotation.NonNull;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

public class PncEventClient {

    private Event event;
    private Client client;

    public PncEventClient(@NonNull Client client, @NonNull Event event) {
        this.client = client;
        this.event = event;
    }

    @NonNull
    public Client getClient() {
        return client;
    }

    @NonNull
    public Event getEvent() {
        return event;
    }
}
