package models;

import play.modules.morphia.Model;
import com.greenlaw110.play.api.IUser;

public class User extends Model implements IUser {
    public static User current() {
        return null;
    }
}
