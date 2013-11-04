package risible.demo.app.controller;


import risible.core.MediaType;
import risible.core.annotations.PathParam;
import risible.core.annotations.Produces;
import risible.core.annotations.QueryParam;
import risible.demo.app.UserService;
import risible.demo.app.domain.User;

import javax.annotation.Resource;
import java.util.List;

public class Users {
    @Resource
    private UserService userService;

    @QueryParam
    private User user;

    public User getUser() {
        return user;
    }

    public List<User> newUser(@QueryParam("user") User aUser) {
        if (aUser != null && null != aUser.getFirstName() && null != aUser.getLastName()
                && !aUser.getFirstName().isEmpty() && !aUser.getLastName().isEmpty()) {
            userService.persist(aUser);
        }

        return userService.users();
    }

    public String addUser() {
        if (user != null && null != user.getFirstName() && null != user.getLastName()
                && !user.getFirstName().isEmpty() && !user.getLastName().isEmpty()) {
            userService.persist(user);
        }

        return "usersTable";
    }

    public String redirect(){
        return "redirect:list.html" ;
    }

    public void list() {
    }

    @Produces(MediaType.APPLICATION_JSON)
    public List<User> listAsJson() {
        return userService.users();
    }

    private List<User> listAsJsonNoAnnotation() {
        return userService.users();
    }

    @Produces(MediaType.APPLICATION_JSON)
    public User user(@PathParam(0) Integer id) {
        return userService.findUser(id);
    }

    @Produces(MediaType.APPLICATION_JSON)
    public User findUser(@QueryParam("userId") Integer id) {
        return userService.findUser(id);
    }

    public List<User> getUsers() {
        return userService.users();
    }
}
