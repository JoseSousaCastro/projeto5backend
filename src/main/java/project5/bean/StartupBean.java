package project5.bean;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;


@Singleton
@Startup
public class StartupBean {
    @Inject
    UserBean userBean;

    private static final long serialVersionUID = 1L;


    @PostConstruct
    public void init() {
        userBean.createDefaultUsersIfNotExistent();
    }
}