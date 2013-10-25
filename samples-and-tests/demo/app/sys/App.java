package sys;

import models.User;
import org.osgl.play.api.IApplication;
import org.osgl.play.api.IUser;

public class App implements IApplication {

    @Override
    public IUser currentUser() {
        return User.current();
    }

}
