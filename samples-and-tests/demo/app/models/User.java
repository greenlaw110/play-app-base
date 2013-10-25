package models;

import play.modules.morphia.Model;
import org.osgl.play.api.IUser;

public class User extends Model implements IUser {
    public static User current() {
        return null;
    }
}
