package sys;

import models.User;
import com.greenlaw110.play.api.IApplication;
import com.greenlaw110.play.api.IUser;

public class App implements IApplication {

    @Override
    public IUser currentUser() {
        return User.current();
    }

}
