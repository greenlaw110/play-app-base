package api;

import play.db.Model;

public interface IApplication {
    /**
     * Return current connected user
     * @return
     */
    IUser currentUser();
}
