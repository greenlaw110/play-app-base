package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;
import controllers.filters.*;

@With(Config.class)
public class Application extends Controller {

    public static void index() {
        render();
    }

}