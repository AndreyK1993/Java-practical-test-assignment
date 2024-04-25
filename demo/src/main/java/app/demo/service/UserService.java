package app.demo.service;

import app.demo.entity.User;
import app.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final int MINIMUM_AGE = 18;

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<Object> createUser(User user) {
        // Validation for minimum age
        if (!isAdult(user.getBirthDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User must be at least " + MINIMUM_AGE + " years old.");
        }

        if (userRepository.createUser(user)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create user");
        }
    }

    public ResponseEntity<Object> updateUser(Long userId, User user) {
        // Check if the user exists
        ResponseEntity<Object> responseEntity = getUserById(userId);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return responseEntity;
        }

        user.setId(userId);
        if (userRepository.updateUser(user)) {
            return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user");
        }
    }

    public ResponseEntity<String> updateAllUserFields(Long userId, User user) {
        Optional<User> existingUserOptional = userRepository.getUserById(userId);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            // Обновляем все поля пользователя на переданные значения
            existingUser.setEmail(user.getEmail());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setBirthDate(user.getBirthDate());
            existingUser.setAddress(user.getAddress());
            existingUser.setPhoneNumber(user.getPhoneNumber());

            // Сохраняем обновленного пользователя
            if (userRepository.updateUser(existingUser)) {
                return ResponseEntity.status(HttpStatus.OK).body("All user fields updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + userId);
        }
    }


    public ResponseEntity<Object> deleteUser(Long userId) {
        // Check if the user exists
        ResponseEntity<Object> responseEntity = getUserById(userId);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return responseEntity;
        }

        if (userRepository.deleteUser(userId)) {
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        // Check if the user exists
        if (!userRepository.getUserById(userId).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + userId);
        }

        User user = userRepository.getUserById(userId).get();
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    public ResponseEntity<Object> getAllUsers() {
        List<User> users = userRepository.getAllUsers().orElse(null);
        if (users != null) {
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve users");
        }
    }

    public ResponseEntity<Object> getUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid date range: 'From' date must be before 'To' date");
        }

        List<User> users = userRepository.getUsersByBirthDateRange(fromDate, toDate).orElse(null);
        if (users != null) {
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve users by birth date range");
        }
    }

    private boolean isAdult(LocalDate birthDate) {
        return birthDate.plusYears(MINIMUM_AGE).isBefore(LocalDate.now());
    }
}
