package app.demo.controller;

import app.demo.entity.Users;
import app.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<Users>> getUsers() {
        List<Users> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Users> createUser(@Valid @RequestBody Users user) {
        Users createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Users> updateUser(@PathVariable("userId") int userId, @Valid @RequestBody Users user) {
        Users updatedUser = userService.updateUser(userId, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PutMapping("/updateAll/{userId}")
    public ResponseEntity<Users> updateAllUserFields(@PathVariable("userId") int userId, @Valid @RequestBody Users user) {
        Users updatedUser = userService.updateAllUserFields(userId, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") int userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Users>> searchUsersByBirthDateRange(@RequestParam("from") Date fromDate, @RequestParam("to") Date toDate) {
        if (fromDate.after(toDate)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Users> users = userService.searchUsersByBirthDateRange(fromDate, toDate);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
