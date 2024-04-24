package app.demo.service;

import app.demo.entity.Users;
import app.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository repository;

    public List<Users> getUsers() {
        return (List<Users>) repository.findAll();
    }

    public Users createUser(Users user) {
        // Дополнительная проверка возраста пользователя
        if (isUserAboveAgeLimit(user.getBirthDate())) {
            return repository.save(user);
        } else {
            throw new IllegalArgumentException("User must be above 18 years old.");
        }
    }

    public Users updateUser(int userId, Users user) {
        Users existingUser = getUserById(userId);
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setBirthDate(user.getBirthDate());
        existingUser.setAddress(user.getAddress());
        existingUser.setPhone(user.getPhone());
        return repository.save(existingUser);
    }

    public void deleteUser(int userId) {
        repository.deleteById(userId);
    }

    public List<Users> searchUsersByBirthDateRange(Date fromDate, Date toDate) {
        List<Users> allUsers = getUsers();
        return allUsers.stream()
                .filter(user -> user.getBirthDate().after(fromDate) && user.getBirthDate().before(toDate))
                .collect(Collectors.toList());
    }


    // Дополнительный метод для проверки возраста пользователя
    private boolean isUserAboveAgeLimit(Date birthDate) {
        // Реализуйте проверку, что пользователю больше 18 лет
        // Здесь предполагается, что вы реализуете эту проверку самостоятельно
        return true;
    }

    private Users getUserById(int userId) {
        Optional<Users> optionalUser = repository.findById(userId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }
}
