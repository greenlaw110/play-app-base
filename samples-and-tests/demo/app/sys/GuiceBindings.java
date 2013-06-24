package sys;

import org.osgl.play.api.IApplication;

import com.google.inject.AbstractModule;

public class GuiceBindings extends AbstractModule {

    @Override
    protected void configure() {
        bind(IApplication.class).to(App.class);
    }

}
