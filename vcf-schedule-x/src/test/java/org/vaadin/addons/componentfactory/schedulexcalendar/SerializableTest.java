package org.vaadin.addons.componentfactory.schedulexcalendar;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.testutil.ClassesSerializableTest;

import java.util.stream.Stream;

public class SerializableTest extends ClassesSerializableTest {

    private static final UI FAKE_UI = new UI();

    @Override
    protected void resetThreadLocals() {
        super.resetThreadLocals();
        UI.setCurrent(null);
    }

    @Override
    protected void setupThreadLocals() {
        super.setupThreadLocals();
        UI.setCurrent(FAKE_UI);
    }

    @Override
    protected Stream<String> getExcludedPatterns() {
        return Stream.concat(super.getExcludedPatterns(), Stream.of(
                "com\\.vaadin\\..*"
        ));
    }
}
