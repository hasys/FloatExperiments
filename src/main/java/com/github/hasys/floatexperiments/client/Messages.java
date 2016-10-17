package com.github.hasys.floatexperiments.client;

import com.google.gwt.i18n.client.LocalizableResource.Generate;

@Generate(format = "com.google.gwt.i18n.server.PropertyCatalogFactory")
public interface Messages extends com.google.gwt.i18n.client.Messages {

    @DefaultMessage("4.13523")
    String floatValue();

    @DefaultMessage("Round it!")
    String send();

    @DefaultMessage("2")
    String roundTo();

    @DefaultMessage("Check values")
    String check();
}
