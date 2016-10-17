package com.github.hasys.floatexperiments.client;

import com.github.hasys.floatexperiments.shared.FieldRounder;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FloatExperiments implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

    private final Messages messages = GWT.create(Messages.class);

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final Button sendButton = new Button(messages.send());
        final Button checkButton = new Button(messages.check());
        final TextBox floatNumber = new TextBox();
        final TextBox roundToNumber = new TextBox();
        floatNumber.setText(messages.floatValue());
        roundToNumber.setText(messages.roundTo());

        // We can add style names to widgets
        sendButton.addStyleName("someButton");
        checkButton.addStyleName("someButton");
        final FlexTable table = new FlexTable();
        table.setWidth("800px");
        table.getElement().getStyle().setProperty("margin", "0 auto");
        table.getRowFormatter().addStyleName(0, "tableHeader");
        table.setText(0, 0, "Original:");
        table.setText(0, 1, "Round to:");
        table.setText(0, 2, "Client GWT");
        table.setText(0, 3, "toFixed JS:");
        table.setText(0, 4, "Server side:");

        addRowToTable(table, "0.256713", 4);
        addRowToTable(table, "0.256713", 2);
        addRowToTable(table, "0.256713", 5);
        addRowToTable(table, "0.256713", 0);
        addRowToTable(table, "0.256713", 1);
        addRowToTable(table, "0.259983", 4);
        addRowToTable(table, "0.259933", 4);
        addRowToTable(table, "3.99912", 4);
        addRowToTable(table, "3.99923", 3);
        addRowToTable(table, "3.99923", 0);

        RootPanel.get("doubleFieldContainer").add(floatNumber);
        RootPanel.get("roundToFieldContainer").add(roundToNumber);
        RootPanel.get("sendButtonContainer").add(sendButton);
        RootPanel.get("checkButtonContainer").add(checkButton);
        RootPanel.get("flexibleResults").add(table);

        // Focus the cursor on the name field when the app loads
        floatNumber.setFocus(true);
        floatNumber.selectAll();

        sendButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                addRowToTable(table, floatNumber.getText(), Integer.parseInt(roundToNumber.getText()));
            }
        });
        checkButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                for (int i = 1; i < table.getRowCount(); i++) {
                    String javaClientRound = table.getText(i, 2);
                    String jsRound = table.getText(i, 3);
                    String javaServerRound = table.getText(i, 4);
                    if (!javaClientRound.equals(jsRound)) {
                        table.getFlexCellFormatter().setStyleName(i, 2, "different");
                        table.getFlexCellFormatter().setStyleName(i, 3, "original");
                    }

                    if (!javaServerRound.equals(jsRound)) {
                        table.getFlexCellFormatter().setStyleName(i, 4, "different");
                        table.getFlexCellFormatter().setStyleName(i, 3, "original");
                    }
                }
            }
        });
    }

    private void addRowToTable(final FlexTable table, final String floatValue, final int roundTo) {
        final int n = table.getRowCount();
        table.getRowFormatter().addStyleName(n, (n%2 == 0 ? "even" : "odd"));
        table.setText(n, 0, floatValue);
        table.setText(n, 1, Integer.toString(roundTo));
        table.setText(n, 2, FieldRounder.toFixed(Float.parseFloat(floatValue), roundTo));
        table.setHTML(n, 3, FieldRounder.toFixedJS(Double.parseDouble(floatValue), roundTo));
        Button tryOnServerSide = new Button();
        tryOnServerSide.setText("Calculate on server side");
        tryOnServerSide.addAttachHandler(new AttachEvent.Handler() {
            @Override public void onAttachOrDetach(AttachEvent event) {
                greetingService.roundValue(floatValue, Integer.toString(roundTo), new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                    }

                    public void onSuccess(String result) {
                        table.setHTML(n, 4, result);
                    }
                });
            }
        });
        table.setWidget(n, 4, tryOnServerSide);
        tryOnServerSide.click();
    }
}
