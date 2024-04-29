package project5.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;
import project5.bean.*;
import project5.dao.TokenExpirationDao;
import project5.dao.UserDao;
import project5.dto.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import project5.entity.TokenExpirationEntity;
import project5.entity.UserEntity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.*;

@Path("/users")
public class UserService implements Serializable {

    @Inject
    UserBean userBean;
    @Inject
    TaskBean taskBean;
    @Inject
    CategoryBean categoryBean;
    @Inject
    EmailBean emailBean;
    @Inject
    StatsBean statsBean;
    @Inject
    UserDao userDao;
    @Inject
    ChatBean chatBean;
    @Inject
    TokenExpirationDao tokenExpirationDao;

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(UserService.class);


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Login login, @Context HttpServletRequest request) {

        LoggedUser loggedUser = userBean.login(login);
        Response response;
        if (loggedUser != null) {
            logger.info("User '{}' logged in. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    loggedUser.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            UserEntity userEntity = userDao.findUserByUsername(loggedUser.getUsername());
            if (userEntity != null) {
                logger.info("User '{}' found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        loggedUser.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                userBean.createTokenTimeoutForUser(userEntity);
                logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        loggedUser.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            }
            response = Response.status(200).entity(loggedUser).build();
            logger.info("User '{}' logged in. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    loggedUser.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
            logger.info("Failed login attempt. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        }
        return response;
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@HeaderParam("token") String token, @Context HttpServletRequest request) {

        logger.info("User '{}' logged out. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userBean.convertEntityByToken(token).getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.logout(token))
            return Response.status(200).entity("Logout Successful!").build();
        logger.info("Failed logout attempt. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        return Response.status(401).entity("Invalid Token!").build();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(@HeaderParam("token") String token, User user, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User '{}' is trying to register a new user {}. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean isUsernameAvailable = userBean.isUsernameAvailable(user);
        logger.info("Username '{}' is available: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getUsername(), isUsernameAvailable, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean isEmailValid = userBean.isEmailValid(user);
        logger.info("Email '{}' is valid: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getEmail(), isEmailValid, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean isFieldEmpty = userBean.isAnyFieldEmpty(user);
        logger.info("All registration form fields have been filled in: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                isFieldEmpty, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean isPhoneNumberValid = userBean.isPhoneNumberValid(user);
        logger.info("Phone number '{}' is valid: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getPhone(), isPhoneNumberValid, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean isImageValid = userBean.isImageUrlValid(user.getPhotoURL());
        logger.info("Image URL '{}' is valid: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getPhotoURL(), isImageValid, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        long expirationTime = System.currentTimeMillis() + 48 * 60 * 60 * 1000; // 48 horas em milissegundos
        logger.info("Expiration time for defining the password started at: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                expirationTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        user.setExpirationTime(expirationTime);
        user.setTokenExpirationTime(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000); // 3 dias em milissegundos
        user.setConfirmed(false);
        logger.info("User '{}' is not confirmed yet. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        user.setCreationDate(LocalDate.of(2021, 1, 1));
        logger.info("User '{}' was created at: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getUsername(), user.getCreationDate(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean newPassword = emailBean.sendConfirmationEmail(user);
        logger.info("Email was sent to '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getEmail(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());

        if (isFieldEmpty) {
            logger.info("User registration failed. There's an empty field. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(422).entity("There's an empty field. ALl fields must be filled in").build();
        } else if (!isEmailValid) {
            logger.info("User registration failed. Email '{}' is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getEmail(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(422).entity("Invalid email").build();
        } else if (!isUsernameAvailable) {
            logger.info("User registration failed. Username '{}' is already in use. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.CONFLICT).entity("Username already in use").build(); //status code 409
        } else if (!isImageValid) {
            logger.info("User registration failed. Image URL '{}' is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getPhotoURL(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(422).entity("Image URL invalid").build(); //400
        } else if (!isPhoneNumberValid) {
            logger.info("User registration failed. Phone number '{}' is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getPhone(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(422).entity("Invalid phone number").build();
        } else if (!newPassword) {
            logger.info("User registration failed. Email was not sent. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(404).entity("Email not sent").build();
        } else if (userBean.register(user)) {
            logger.info("User '{}' registered successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.CREATED).entity("User registered successfully").build(); //status code 201
        } else {
            logger.info("User registration failed. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong").build(); //status code 400
        }
        return response;
    }

    @GET
    @Path("/getFirstName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFirstName(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get his first name. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User currentUser = userBean.convertEntityByToken(token);
        logger.info("User '{}' got his first name successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                currentUser.getFirstName(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!userBean.isAuthenticated(token)) {
            logger.info("User is not authenticated to get his first name. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        } else {
            logger.info("User '{}' got his first name successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    currentUser.getFirstName(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(currentUser.getFirstName()).build();
        }
        return response;
    }

    //Retorna o url da foto do token enviado
    @GET
    @Path("/getPhotoUrl")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImage(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get his photo URL. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User currentUser = userBean.convertEntityByToken(token);
        logger.info("User '{}' got his photo URL successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                currentUser.getPhotoURL(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!userBean.isAuthenticated(token)) {
            logger.info("User is not authenticated to get his photo URL. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        } else {
            logger.info("User '{}' got his photo URL successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    currentUser.getPhotoURL(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(currentUser.getPhotoURL()).build();
        }
        return response;
    }

    //Retorna username do token enviado
    @GET
    @Path("/getUsername")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsername(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get his username. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User currentUser = userBean.convertEntityByToken(token);
        logger.info("User '{}' got his username successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                currentUser.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!userBean.isAuthenticated(token)) {
            logger.info("User is not authenticated to get his username. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        } else {
            logger.info("User '{}' got his username successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    currentUser.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(currentUser.getUsername()).build();
        }
        return response;
    }


    //Retorna tipo de user do token enviado
    @GET
    @Path("/getTypeOfUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTypeOfUser(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get his type of user. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User currentUser = userBean.convertEntityByToken(token);
        logger.info("User '{}' got his type of user successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                currentUser.getTypeOfUser(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!userBean.isAuthenticated(token)) {
            logger.info("User is not authenticated to get his type of user. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        } else {
            logger.info("User '{}' got his type of user successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    currentUser.getTypeOfUser(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(currentUser.getTypeOfUser()).build();
        }
        return response;
    }

    @GET
    @Path("/getUsernameFromEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsernameFromEmail(@HeaderParam("email") String email, @HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get his username from email. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.convertEntityByEmail(email);
        logger.info("User '{}' got his username from email successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!userBean.isAuthenticated(token)) {
            logger.info("User is not authenticated to get his username from email. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        } else {
            logger.info("User '{}' got his username from email successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(user.getUsername()).build();
        }
        return response;
    }

    //Atualizar um user
    @PUT
    @Path("/update/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("username") String username, @HeaderParam("token") String token, User user, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User '{}' is trying to update his information. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User userUpdate = userBean.getUser(username);
        logger.info("User '{}' got his information successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userUpdate, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userUpdate == null) {
            logger.info("User '{}' is not found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            return response;
        }
        if (userBean.isAuthenticated(token) && userBean.userIsProductOwner(token) || userBean.thisTokenIsFromThisUsername(token, username)) {
            logger.info("User '{}' is authenticated and is a Product Owner. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (!userBean.isEmailUpdatedValid(user) && user.getEmail() != null) {
                logger.info("User '{}' email is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(422).entity("Invalid email").build();
            } else if (!userBean.isImageUrlUpdatedValid(user.getPhotoURL()) && user.getPhotoURL() != null) {
                logger.info("User '{}' image URL is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(422).entity("Image URL invalid").build();
            } else if (!userBean.isPhoneNumberUpdatedValid(user) && user.getPhone() != null) {
                logger.info("User '{}' phone number is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(422).entity("Invalid phone number").build();
            } else {
                logger.info("User '{}' information was updated successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                boolean updatedUser = userBean.updateUser(user, username);
                response = Response.status(Response.Status.OK).entity(updatedUser).build();
            }
        } else {
            logger.info("User '{}' is not authenticated or is not a Product Owner. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }


    @PUT
    @Path("/update/{username}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(@PathParam("username") String username,
                                   @HeaderParam("token") String token,
                                   @HeaderParam("oldpassword") String oldPassword,
                                   @HeaderParam("newpassword") String newPassword, @Context HttpServletRequest request) {
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User '{}' is trying to update his password. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User '{}' is authenticated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            boolean isOldPasswordValid = userBean.verifyOldPassword(username, oldPassword);
            if (!isOldPasswordValid) {
                logger.info("User '{}' old password is incorrect. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                return Response.status(401).entity("Incorrect old password").build();
            }
            logger.info("User '{}' old password is correct. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            boolean updated = userBean.updatePassword(username, newPassword);
            if (!updated) {
                logger.info("User '{}' password was not updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                return Response.status(400).entity("User with this username is not found").build();
            } else
                logger.info("User '{}' password was updated successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(200).entity("User password updated").build();
        } else
            logger.info("User '{}' is not authenticated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        return Response.status(401).entity("User is not logged in").build();
    }

    @PUT
    @Path("/{username}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(@PathParam("username") String username,
                                   @HeaderParam("newpassword") String newPassword, @HeaderParam("token") String token, @Context HttpServletRequest request) {
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User '{}' is trying to update his password. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.getUser(username);
        logger.info("User '{}' got his information successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (user == null) {
            logger.info("User '{}' is not found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(404).entity("User with this username is not found").build();
        }
        long expirationTime = user.getExpirationTime();
        logger.info("User '{}' expiration time is: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, expirationTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        long currentTime = System.currentTimeMillis();
        logger.info("User '{}' current time is: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, currentTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (expirationTime != 0 && currentTime > expirationTime) {
            logger.info("User '{}' link expired. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            user.setExpirationTime(0);
            logger.info("User '{}' expiration time is now: 0. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(401).entity("Link expired").build();
        }
        logger.info("User '{}' link is not expired. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean updated = userBean.updatePassword(user.getUsername(), newPassword);
        if (!updated) {
            logger.info("User '{}' password was not updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(400).entity("User with this username is not found").build();
        } else
            logger.info("User '{}' password was updated successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        return Response.status(200).entity("Password updated").build();
    }

    @POST
    @Path("/recover-password")
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendRecoverPasswordEmail(@HeaderParam("email") String email, @HeaderParam("token") String token, @Context HttpServletRequest request) {
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to recover his password. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.getUserByEmail(email);
        logger.info("User '{}' got his information successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (user == null) {
            logger.info("User with email '{}' not found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    email, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(404).entity("User with this email not found").build();
        }
        long expirationTime = System.currentTimeMillis() + 2 * 60 * 1000; // 2 minutos em milissegundos
        logger.info("User '{}' expiration time is: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, expirationTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        user.setExpirationTime(expirationTime);
        boolean newEmailNemPassword = emailBean.sendPasswordResetEmail(user);
        logger.info("Email was sent to '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getEmail(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!newEmailNemPassword) {
            logger.info("Email was not sent to '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getEmail(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(404).entity("Email not sent").build();
        }
        logger.info("Email was sent to '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getEmail(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        return Response.status(200).entity("Email sent").build();
    }

    @PUT
    @Path("/{username}/reset-password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetPassword(@PathParam("username") String username,
                                  @HeaderParam("newpassword") String newPassword, @HeaderParam("token") String token, @Context HttpServletRequest request) {
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User '{}' is trying to reset his password. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.getUser(username);
        logger.info("User '{}' got his information successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (user == null) {
            logger.info("User with username '{}' not found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(404).entity("User with this username is not found").build();
        } else if (!user.isConfirmed()) {
            logger.info("User '{}' is not confirmed. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(401).entity("User is not confirmed").build();
        }
        long expirationTime = user.getExpirationTime();
        logger.info("User '{}' expiration time is: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, expirationTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        long currentTime = System.currentTimeMillis();
        logger.info("User '{}' current time is: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, currentTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (expirationTime != 0 && currentTime > expirationTime) {
            logger.info("User '{}' link expired. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            user.setExpirationTime(0);
            logger.info("User '{}' expiration time is now: 0. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(401).entity("Link expired").build();
        }
        logger.info("User '{}' link is not expired. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean updated = userBean.updatePassword(user.getUsername(), newPassword);
        if (!updated) {
            logger.info("User '{}' password was not updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(400).entity("User with this username is not found").build();
        }
        logger.info("User '{}' password was updated successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        return Response.status(200).entity("Password updated").build();
    }


    @PUT
    @Path("/update/{username}/visibility")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateVisibility(@PathParam("username") String username, @HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User '{}' is trying to update his visibility. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.getUser(username);
        logger.info("User '{}' got his information successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (user == null) {
            logger.info("User '{}' is not found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            return response;
        }
        logger.info("User '{}' is found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token) && userBean.userIsProductOwner(token)) {
            logger.info("User '{}' is authenticated and is a Product Owner. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            userBean.updateUserEntityVisibility(username);
            logger.info("User '{}' visibility was updated successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.OK).entity(username + " visibility: " + !user.isVisible()).build(); //status code 200
        } else {
            logger.info("User '{}' is not authenticated or is not a Product Owner. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }


    @PUT
    @Path("/update/{username}/role")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRole(@PathParam("username") String username, @HeaderParam("token") String token, @HeaderParam("typeOfUser") int typeOfUser, @Context HttpServletRequest request) {
        String usernamePO = userBean.convertEntityByToken(token).getUsername();
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User '{}' is trying to update {} role. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                usernamePO, username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.getUser(username);
        logger.info("User '{}' got {} information successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                usernamePO, username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (user == null) {
            logger.info("User '{}' is not found. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            return response;
        }
        logger.info("User '{}' is found. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token) && userBean.userIsProductOwner(token)) {
            logger.info("User '{}' is authenticated and is a Product Owner. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    usernamePO, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (typeOfUser == 100 || typeOfUser == 200 || typeOfUser == 300) {
                logger.info("User '{}' type of user is valid. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                userBean.updateUserEntityRole(username, typeOfUser);
                logger.info("User '{}' role was updated successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(Response.Status.OK).entity("Role updated with success").build(); //status code 200
            } else {
                logger.info("User '{}' type of user is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(401).entity("Invalid type of User").build();
            }
        } else {
            logger.info("User '{}' is not authenticated or is not a Product Owner. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, usernamePO, request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }


    @DELETE
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeUser(@HeaderParam("token") String token, @PathParam("username") String username, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User '{}' is trying to remove his account. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User '{}' is authenticated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            boolean removed = userBean.delete(username);
            logger.info("User '{}' was removed successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (removed) {
                logger.info("User '{}' was removed successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(200).entity("User removed successfully").build();
            } else {
                logger.info("User '{}' was not removed. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(404).entity("User is not found").build();
            }
        } else {
            logger.info("User '{}' is not authenticated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("Token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get all users. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to get all users. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            List<User> allUsers = userBean.getUsers();
            logger.info("User got all users successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(allUsers).build();
        } else {
            logger.info("User is not authenticated to get all users. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("You don't have permission").build();
        }
        return response;
    }

    @GET
    @Path("/all/visible/{visible}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersByVisibility(@HeaderParam("token") String token, @PathParam("visible") boolean visible, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get all users by visibility. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token) && !userBean.userIsDeveloper(token)) {
            logger.info("User is authenticated to get all users by visibility. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            List<User> users = userBean.getUsersByVisibility(visible);
            logger.info("User got all users by visibility successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(users).build();
        } else {
            logger.info("User is not authenticated to get all users by visibility. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("You don't have permission").build();
        }
        return response;
    }

    @GET
    @Path("/all/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@HeaderParam("token") String token, @PathParam("type") int typeOfUser, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get all users by type. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token) && !userBean.userIsDeveloper(token)) {
            logger.info("User is authenticated to get all users by type. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            List<User> users = userBean.getUsersByType(typeOfUser);
            logger.info("User got all users by type successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(users).build();
        } else {
            logger.info("User is not authenticated to get all users by type. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("You don't have permission").build();
        }
        return response;
    }

    @GET
    @Path("/all/{type}/{visible}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@HeaderParam("token") String token, @PathParam("type") int typeOfUser, @PathParam("visible") boolean visible, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get all users by type and visibility. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token) && !userBean.userIsDeveloper(token)) {
            logger.info("User is authenticated to get all users by type and visibility. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            List<User> users = userBean.getUsersByTypeAndVisibility(typeOfUser, visible);
            logger.info("User got all users by type and visibility successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(users).build();
        } else {
            logger.info("User is not authenticated to get all users by type and visibility. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("You don't have permission").build();
        }
        return response;
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("username") String username, @HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get his information. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User userSearched = userBean.getUser(username);
        logger.info("User '{}' got his information successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                userSearched, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userSearched == null) {
            logger.info("User '{}' is not found. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            return response;
        }
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to get his information. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.ok().entity(userSearched).build();
        } else {
            logger.info("User is not authenticated to get his information. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasks(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get all tasks. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to get all tasks. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            ArrayList<Task> allTasks = taskBean.getAllTasks(token);
            logger.info("User got all tasks successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.OK).entity(allTasks).build();
        } else {
            logger.info("User is not authenticated to get all tasks. Author: '{}. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/{username}/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasksFromUser(@HeaderParam("token") String token, @PathParam("username") String username, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to get all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (userBean.thisTokenIsFromThisUsername(token, username) || userBean.userIsProductOwner(token) || userBean.userIsScrumMaster(token)) {
                logger.info("User is authenticated to get all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                ArrayList<Task> userTasks = taskBean.getAllTasksFromUser(username, token);
                logger.info("User got all tasks from user '{}' successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(Response.Status.OK).entity(userTasks).build();
            } else {
                logger.info("User is not authenticated to get all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(406).entity("You don't have permission for this request").build();
            }
        } else {
            logger.info("User is not authenticated to get all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @POST
    @Path("/{username}/addTask")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newTask(@HeaderParam("token") String token, @PathParam("username") String username, Task task, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to add a new task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to add a new task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (userBean.thisTokenIsFromThisUsername(token, username)) {
                logger.info("User is authenticated to add a new task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                try {
                    logger.info("User is trying to add a new task to user '{}' successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                            username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    boolean added = taskBean.newTask(task, token);
                    logger.info("User added a new task to user '{}' successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                            username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    if (added) {
                        logger.info("User added a new task to user '{}' successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(201).entity("Task created successfully").build();
                    } else {
                        logger.info("User was not able to add a new task to user '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(404).entity("Impossible to create task. Verify all fields").build();
                    }
                } catch (Exception e) {
                    logger.info("User was not able to add a new task to user '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                            username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    response = Response.status(404).entity("Something went wrong. A new category was not created.").build();
                }
            } else {
                logger.info("User is not authenticated to add a new task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(Response.Status.BAD_REQUEST).entity("Invalid username on path").build();
            }
        } else {
            logger.info("User is not authenticated to add a new task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @PUT
    @Path("/updatetask/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTask(@HeaderParam("token") String token, @PathParam("id") String id, Task task, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to update a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to update a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (userBean.userIsTaskOwner(token, id) || userBean.userIsScrumMaster(token) || userBean.userIsProductOwner(token)) {
                logger.info("User has permission to update a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                boolean updated = taskBean.updateTask(task, id);
                logger.info("User updated a task successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                if (updated) {
                    logger.info("User updated a task successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                            request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    response = Response.status(200).entity("Task updated successfully").build();
                } else {
                    logger.info("User was not able to update a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                            request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    response = Response.status(404).entity("Impossible to update task. Verify all fields").build();
                }
            } else {
                logger.info("User does not have permission to update a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(403).entity("You don't have permission to update this task").build();
            }
        } else {
            logger.info("User is not authenticated to update a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }


    @PUT
    @Path("/tasks/{taskId}/{newStateId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTaskStatus(@HeaderParam("token") String token, @PathParam("taskId") String taskId, @PathParam("newStateId") int stateId, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to update a task status. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to update a task status. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            boolean updated = taskBean.updateTaskStatus(taskId, stateId);
            logger.info("User updated a task status successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (updated) {
                logger.info("Task status updated successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(200).entity("Task status updated successfully").build();
            } else {
                logger.info("Task status was not updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(404).entity("Impossible to update task status. Task not found or invalid status").build();
            }
        } else {
            logger.info("User is not authenticated to update a task status. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @PUT
    @Path("/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response eraseTask(@HeaderParam("token") String token, @PathParam("taskId") String id, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to erase a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to erase a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (userBean.userIsScrumMaster(token) || userBean.userIsProductOwner(token)) {
                logger.info("User has permission to erase a task. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                try {
                    logger.info("User is trying to erase a task successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                            request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    boolean switched = taskBean.switchErasedTaskStatus(id);
                    if (switched) {
                        logger.info("User erased a task successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(200).entity("Task erased status switched successfully").build();
                    } else {
                        logger.info("Task erased status was not switched. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(404).entity("Task with this id is not found").build();
                    }
                } catch (Exception e) {
                    logger.info("Task erased status was not switched. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                            request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    response = Response.status(404).entity("Something went wrong. The task erased status was switched.").build();
                }
            } else {
                logger.info("User does not have permission to erase a task. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(403).entity("You don't have permission to switch the erased status of a task").build();
            }
        } else {
            logger.info("User is not authenticated to erase a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @PUT
    @Path("/eraseAllTasks/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response eraseAllTasksFromUser(@HeaderParam("token") String token, @PathParam("username") String username, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to erase all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to erase all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (userBean.userIsProductOwner(token)) {
                logger.info("User has permission to erase all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                try {
                    logger.info("User is trying to erase all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                            username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    boolean erased = taskBean.eraseAllTasksFromUser(username);
                    if (erased) {
                        logger.info("User erased all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(200).entity("All tasks were erased successfully").build();
                    } else {
                        logger.info("All tasks from user '{}' were not erased. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(404).entity("Impossible to erase tasks").build();
                    }
                } catch (Exception e) {
                    logger.info("All tasks from user '{}' were not erased. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                            username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    response = Response.status(404).entity("Something went wrong. The tasks were not erased.").build();
                }
            } else {
                logger.info("User does not have permission to erase all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(403).entity("You don't have permission to erase these tasks").build();
            }
        } else {
            logger.info("User is not authenticated to erase all tasks from user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @DELETE
    @Path("/delete/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteTask(@HeaderParam("token") String token, @PathParam("taskId") String id, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to delete a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to delete a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (userBean.userIsProductOwner(token)) {
                logger.info("User has permission to delete a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                try {
                    logger.info("User is trying to delete a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                            request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    boolean deleted = taskBean.permanentlyDeleteTask(id);
                    if (deleted) {
                        logger.info("User deleted a task successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(200).entity("Task removed successfully").build();
                    } else {
                        logger.info("Task was not deleted. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(404).entity("Task with this id is not found").build();
                    }
                } catch (Exception e) {
                    logger.info("Task was not deleted. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                            request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    response = Response.status(404).entity("Something went wrong. The task was not removed.").build();
                }
            } else {
                logger.info("User does not have permission to delete a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(403).entity("You don't have permission to delete a task").build();
            }
        } else {
            logger.info("User is not authenticated to delete a task. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/tasks/{category}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTasksByCategory(@HeaderParam("token") String token, @PathParam("category") String category, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get tasks by category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to get tasks by category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (userBean.userIsScrumMaster(token) || userBean.userIsProductOwner(token)) {
                logger.info("User has permission to get tasks by category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                ArrayList<Task> tasksByCategory = taskBean.getTasksByCategory(category);
                logger.info("User got tasks by category successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(200).entity(tasksByCategory).build();
            } else {
                logger.info("User does not have permission to get tasks by category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(403).entity("You don't have permission for this request").build();
            }
        } else {
            logger.info("User is not authenticated to get tasks by category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/erasedTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getErasedTasks(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get erased tasks. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to get erased tasks. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (userBean.userIsScrumMaster(token) || userBean.userIsProductOwner(token)) {
                logger.info("User has permission to get erased tasks. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                ArrayList<Task> erasedTasks = taskBean.getErasedTasks();
                logger.info("User got erased tasks successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(200).entity(erasedTasks).build();
            } else {
                logger.info("User does not have permission to get erased tasks. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(403).entity("You don't have permission for this request").build();
            }
        } else {
            logger.info("User is not authenticated to get erased tasks. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @POST
    @Path("/newCategory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCategory(@HeaderParam("token") String token, Category category, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to create a new category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to create a new category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (userBean.userIsProductOwner(token)) {
                logger.info("User has permission to create a new category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                if (categoryBean.categoryExists(category.getName())) {
                    logger.info("Category with this name already exists. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                            request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    response = Response.status(409).entity("Category with this name already exists").build();
                } else {
                    try {
                        logger.info("User is trying to create a new category successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        boolean added = categoryBean.newCategory(category.getName());
                        if (added) {
                            logger.info("User created a new category successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                            response = Response.status(201).entity("Category created successfully").build();
                        } else {
                            logger.info("Category was not created. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                            response = Response.status(404).entity("Impossible to create category. Verify all fields").build();
                        }
                    } catch (Exception e) {
                        logger.info("Category was not created. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(404).entity("Something went wrong. A new category was not created.").build();
                    }
                }
            } else {
                logger.info("User does not have permission to create a new category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(403).entity("You don't have permission to create a category").build();
            }
        } else {
            logger.info("User is not authenticated to create a new category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @DELETE
    @Path("/deleteCategory/{categoryName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteCategory(@HeaderParam("token") String token, @PathParam("categoryName") String categoryName, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to delete a category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to delete a category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (userBean.userIsProductOwner(token)) {
                logger.info("User has permission to delete a category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                try {
                    logger.info("User is trying to delete a category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                            request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    boolean deleted = categoryBean.deleteCategory(categoryName);
                    if (deleted) {
                        logger.info("User deleted a category successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(200).entity("Category removed successfully").build();
                    } else {
                        logger.info("Category was not deleted. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(400).entity("Category with this name can't be deleted").build();
                    }
                } catch (Exception e) {
                    logger.info("Category was not deleted. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                            request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    response = Response.status(404).entity("Something went wrong. The category was not removed.").build();
                }
            } else {
                logger.info("User does not have permission to delete a category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(403).entity("You don't have permission to delete a category").build();
            }
        } else {
            logger.info("User is not authenticated to delete a category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @PUT
    @Path("/editCategory/{categoryName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editCategory(@HeaderParam("token") String token, @PathParam("categoryName") String categoryName, @HeaderParam("newCategoryName") String newCategoryName, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to edit a category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to edit a category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (userBean.userIsProductOwner(token)) {
                logger.info("User has permission to edit a category. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                try {
                    logger.info("User is trying to edit a category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                            request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    boolean edited = categoryBean.editCategory(categoryName, newCategoryName);
                    if (edited) {
                        logger.info("User edited a category successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(200).entity("Category edited successfully").build();
                    } else {
                        logger.info("Category with required name is not found. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                        response = Response.status(404).entity("Category with this name is not found").build();
                    }
                } catch (Exception e) {
                    logger.info("Category was not edited. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                            request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                    response = Response.status(404).entity("Something went wrong. The category was not edited.").build();
                }
            } else {
                logger.info("User does not have permission to edit a category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(403).entity("You don't have permission to edit a category").build();
            }
        } else {
            logger.info("User is not authenticated to edit a category. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategories(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get all categories. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to get all categories. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            try {
                logger.info("User is trying to get all categories successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                List<Category> allCategories = categoryBean.findAllCategories();
                logger.info("User got all categories successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(200).entity(allCategories).build();
            } catch (Exception e) {
                logger.info("All categories were not found. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(404).entity("Something went wrong. The categories were not found.").build();
            }
        } else {
            logger.info("User is not authenticated to get all categories. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }


    @GET
    @Path("/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllStats(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get all stats. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to get all stats. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            try {
                logger.info("User is trying to get all stats successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                int totalUsers = statsBean.getNumberOfUsers();
                logger.info("Total users: '{}'.", totalUsers);
                int totalConfirmedUsers = statsBean.getNumberOfConfirmedUsers();
                logger.info("Total confirmed users: '{}'.", totalConfirmedUsers);
                int totalUnconfirmedUsers = statsBean.getNumberOfUnconfirmedUsers();
                logger.info("Total unconfirmed users: '{}'.", totalUnconfirmedUsers);
                ArrayList<RegistInfoUser> usersOverTime = statsBean.getSumOfUsersPerMonth();
                logger.info("Users over time: '{}'.", usersOverTime);

                int totalTasks = statsBean.getNumberOfTasks();
                logger.info("Total tasks: '{}'.", totalTasks);
                int totalToDoTasks = statsBean.getNumberOfTodoTasks();
                logger.info("Total to do tasks: '{}'.", totalToDoTasks);
                int totalDoingTasks = statsBean.getNumberOfDoingTasks();
                logger.info("Total doing tasks: '{}'.", totalDoingTasks);
                int totalDoneTasks = statsBean.getNumberOfDoneTasks();
                logger.info("Total done tasks: '{}'.", totalDoneTasks);
                ArrayList<RegistInfoTask> tasksCompletedOverTime = statsBean.getSumOfCompletedTasksPerMonth();
                logger.info("Tasks completed over time: '{}'.", tasksCompletedOverTime);
                double tasksPerUser = statsBean.getAverageNumberOfTasksPerUser();
                logger.info("Tasks per user: '{}'.", tasksPerUser);
                double averageTaskTime = statsBean.getAverageOfTaskTimes();
                logger.info("Average task time: '{}'.", averageTaskTime);

                ArrayList<RegistInfoCategory> categoriesListDesc = statsBean.getNumberOfCategoriesFromMostFrequentToLeast();
                logger.info("Categories list: '{}'.", categoriesListDesc);
                response = Response.status(200).entity(new Stats(totalUsers, totalConfirmedUsers, totalUnconfirmedUsers, totalTasks, totalToDoTasks,
                        totalDoingTasks, totalDoneTasks, tasksPerUser, averageTaskTime, categoriesListDesc, usersOverTime, tasksCompletedOverTime)).build();
                logger.info("User got all stats successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            } catch (Exception e) {
                logger.info("All stats were not found. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(404).entity("Something went wrong. The stats were not found.").build();
            }
        } else {
            logger.info("User is not authenticated to get all stats. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/{username}/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserStats(@HeaderParam("token") String token, @PathParam("username") String username, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get stats of user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to get stats of user '{}'. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            try {
                int totalUserTasks = statsBean.getNumberOfTasksByUser(username);
                logger.info("Total tasks of user '{}': '{}'.", username, totalUserTasks);
                int totalUserToDoTasks = statsBean.getNumberOfTodoTasksByUser(username);
                logger.info("Total to do tasks of user '{}': '{}'.", username, totalUserToDoTasks);
                int totalUserDoingTasks = statsBean.getNumberOfDoingTasksByUser(username);
                logger.info("Total doing tasks of user '{}': '{}'.", username, totalUserDoingTasks);
                int totalUserDoneTasks = statsBean.getNumberOfDoneTasksByUser(username);
                logger.info("Total done tasks of user '{}': '{}'.", username, totalUserDoneTasks);

                response = Response.status(200).entity(new UserStats(totalUserTasks, totalUserToDoTasks, totalUserDoingTasks, totalUserDoneTasks)).build();
                logger.info("User got stats of user '{}' successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            } catch (Exception e) {
                logger.info("Stats of user '{}' were not found. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(404).entity("Something went wrong. The stats were not found.").build();
            }
        } else {
            logger.info("User is not authenticated to get stats of user '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/getAllMessagesBetweenUsers/{usernameReceiver}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMessagesBetweenUsers(@HeaderParam("token") String token, @PathParam("usernameReceiver") String usernameReceiver, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get all messages between users. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to get all messages between users. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            UserEntity userSender = userDao.findUserByToken(token);
            logger.info("User sender: '{}'.", userSender.getUsername());
            String usernameSender = userSender.getUsername();
            logger.info("User receiver: '{}'.", usernameReceiver);
            ArrayList<ChatMessage> messages = chatBean.getAllChatMessagesBetweenUsers(usernameSender, usernameReceiver);
            logger.info("Messages: '{}'.", messages);
            chatBean.setMessagesAsReadFromSenderToUser(usernameSender, usernameReceiver);
            logger.info("Messages marked as read.");
            response = Response.status(200).entity(messages).build();
            logger.info("User got all messages between users successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        } else {
            logger.info("User is not authenticated to get all messages between users. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/getAllNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUnreadNotifications(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("Token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to get all notifications. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to get all notifications. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            UserEntity userReceiver = userDao.findUserByToken(token);
            logger.info("User receiver: '{}'.", userReceiver.getUsername());
            String usernameReceiver = userReceiver.getUsername();
            logger.info("User receiver: '{}'.", usernameReceiver);
            ArrayList<ChatNotification> notifications = chatBean.getAllNotificationsByReceiver(usernameReceiver);
            logger.info("Notifications: '{}'.", notifications);
            response = Response.status(200).entity(notifications).build();
            logger.info("User got all notifications successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        } else {
            logger.info("User is not authenticated to get all notifications. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @PUT
    @Path("/markNotificationsAsRead/{senderUsername}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response markNotificationsAsRead(@HeaderParam("token") String token, @PathParam("senderUsername") String senderUsername, @Context HttpServletRequest request) {
        Response response;
        UserEntity userEntity = userDao.findUserByToken(token);

        userBean.updateTokenExpirationTime(userEntity);
        logger.info("User '{}' token expiration time updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userEntity.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        logger.info("User is trying to mark notifications as read. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User is authenticated to mark notifications as read. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            UserEntity userReceiver = userDao.findUserByToken(token);
            logger.info("User receiver: '{}'.", userReceiver.getUsername());
            String usernameReceiver = userReceiver.getUsername();
            logger.info("User sender: '{}'.", senderUsername);
            chatBean.setNotificationsAsReadFromSenderToUser(usernameReceiver, senderUsername);
            logger.info("Notifications marked as read.");
            response = Response.status(200).entity("Notifications marked as read").build();
            logger.info("User marked notifications as read successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        } else {
            logger.info("User is not authenticated to mark notifications as read. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @PUT
    @Path("/setTokenTimeout/{time}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setTokenExpirationTime(@PathParam("time") String time, @HeaderParam("token") String token, @Context HttpServletRequest request) {
        long timeInMilliseconds = 0;

        try {
            Long timeLong = Long.valueOf(time);
            System.out.println(timeLong);
            timeInMilliseconds = timeLong * 60 * 1000;
            System.out.println(timeInMilliseconds);
        } catch (NumberFormatException e) {
            // Se a string 'time' não puder ser convertida para long, retorna erro 400
            return Response.status(400).entity("Invalid time format").build();
        }

        if (!userBean.isAuthenticated(token)) {
            logger.info("User is not authenticated to set the token expiration time. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(401).entity("Invalid credentials").build();
        } else if (!userBean.userIsProductOwner(token)) {
            logger.info("User does not have permission to set the token expiration time. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(403).entity("You don't have permission to set the token expiration time").build();
        } else {
            logger.info("User is authenticated to set the token expiration time. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            TokenExpirationEntity tokenExpirationEntity = tokenExpirationDao.findTokenExpirationEntity();
            if (tokenExpirationEntity == null) {
                logger.info("Token expiration entity not found. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                tokenExpirationEntity = new TokenExpirationEntity();
            }
            tokenExpirationEntity.setTokenExpirationTime(timeInMilliseconds);
            System.out.println(tokenExpirationEntity.getTokenExpirationTime());
            tokenExpirationDao.updateTokenExpirationTime(tokenExpirationEntity);
            System.out.println(tokenExpirationEntity.getTokenExpirationTime());
            return Response.status(200).entity("Token expiration time set").build();
        }
    }

}