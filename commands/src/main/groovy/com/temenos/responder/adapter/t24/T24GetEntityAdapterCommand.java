package com.temenos.responder.adapter.t24;

import com.temenos.responder.adapter.AdapterCommand;
import com.temenos.responder.adapter.AdapterParameters;
import com.temenos.responder.entity.runtime.Entity;

public class T24GetEntityAdapterCommand implements AdapterCommand {
    private static T24GetEntityAdapterParameters.T24GetEntityAdapterClientBuilder requestBuilder = T24GetEntityAdapterParameters.builder();

    public static T24GetEntityAdapterParameters.T24GetEntityAdapterClientBuilder requestBuilder() {
        return requestBuilder;
    }

    @Override
    public <P extends AdapterParameters> Entity execute(P parameters) {
        return null;
    }
}
