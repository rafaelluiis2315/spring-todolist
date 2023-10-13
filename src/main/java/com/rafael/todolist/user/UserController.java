package com.rafael.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.rafael.todolist.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private Logger logger;

    @PostMapping("/")
    public ResponseEntity create (@RequestBody UserModel userModel){
        logger.log(userModel.toString());
        logger.log(userModel.getName());
        logger.log(userModel.getUsername());
        logger.log(userModel.getPassword());

        UserModel user = userRepository.findByUsername(userModel.getUsername());

        if (user != null){
            return ResponseEntity.badRequest().body("User already exists");
        }

        String passwordHashed = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());

        userModel.setPassword(passwordHashed);

        UserModel userSaved = userRepository.save(userModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(userSaved);
    }
}
