package sys;

import models.User;
import api.IApplication;
import api.IUser;

public class App implements IApplication {

    @Override
    public IUser currentUser() {
        return User.current();
    }

}
