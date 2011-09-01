package sys;

import api.IApplication;

import com.google.inject.AbstractModule;

public class GuiceBindings extends AbstractModule {

    @Override
    protected void configure() {
        bind(IApplication.class).to(App.class);
    }

}
