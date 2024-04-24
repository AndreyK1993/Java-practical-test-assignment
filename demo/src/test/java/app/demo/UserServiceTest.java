package app.demo;

import app.demo.entity.Users;
import app.demo.repository.UserRepository;
import app.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

	@Mock
	private UserRepository repository;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void getUsers_ReturnsListOfUsers() {
		// Arrange
		List<Users> userList = new ArrayList<>();
		userList.add(new Users(1, "example@example.com", "John", "Doe", new Date(), "Address", "123456789"));
		when(repository.findAll()).thenReturn(userList);

		// Act
		List<Users> result = userService.getUsers();

		// Assert
		assertEquals(userList, result);
	}

	@Test
	void createUser_UserAboveAgeLimit_ReturnsCreatedUser() {
		// Arrange
		Users user = new Users(1, "example@example.com", "John", "Doe", new Date(), "Address", "123456789");
		when(repository.save(user)).thenReturn(user);

		// Act
		Users result = userService.createUser(user);

		// Assert
		assertEquals(user, result);
	}

	@Test
	void createUser_UserBelowAgeLimit_ThrowsException() {
		// Arrange
		Users user = new Users(1, "example@example.com", "John", "Doe", new Date(), "Address", "123456789");
		user.setBirthDate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 17)); // 17 years old
		when(repository.save(user)).thenReturn(user);

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
	}

	@Test
	void updateUser_ReturnsUpdatedUser() {
		// Arrange
		Users existingUser = new Users(1, "old@example.com", "Old", "User", new Date(), "Old Address", "123456789");
		Users updatedUser = new Users(1, "new@example.com", "New", "User", new Date(), "New Address", "987654321");
		when(repository.findById(1)).thenReturn(java.util.Optional.of(existingUser));
		when(repository.save(existingUser)).thenReturn(updatedUser);

		// Act
		Users result = userService.updateUser(1, updatedUser);

		// Assert
		assertEquals(updatedUser, result);
	}

	@Test
	void deleteUser_UserExists_DeletesUser() {
		// Arrange
		Users user = new Users(1, "example@example.com", "John", "Doe", new Date(), "Address", "123456789");

		// Act
		userService.deleteUser(1);

		// Assert
		verify(repository, times(1)).deleteById(1);
	}

	@Test
	void searchUsersByBirthDateRange_ReturnsFilteredUsers() {
		// Arrange
		Date fromDate = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 25); // 25 years ago
		Date toDate = new Date();
		List<Users> userList = new ArrayList<>();
		userList.add(new Users(1, "example1@example.com", "John", "Doe", new Date(), "Address", "123456789"));
		userList.add(new Users(2, "example2@example.com", "Jane", "Smith", fromDate, "Address", "123456789"));
		userList.add(new Users(3, "example3@example.com", "Bob", "Johnson", toDate, "Address", "123456789"));

		// Stubbing getUsers method to return userList
		when(userService.getUsers()).thenReturn(userList);

		// Act
		List<Users> result = userService.searchUsersByBirthDateRange(fromDate, toDate);

		// Assert
		assertEquals(2, result.size());
		assertEquals(userList.get(1), result.get(0));
		assertEquals(userList.get(2), result.get(1));
	}

	
}
