package app.demo.repository;

import app.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (email, first_name, last_name, birth_date, address, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getBirthDate(), user.getAddress(), user.getPhoneNumber()) > 0;
    }

    public Optional<List<User>> getAllUsers() {
        String sql = "SELECT * FROM users";
        try {
            List<User> users = jdbcTemplate.query(sql, new UserRowMapper());
            return Optional.of(users);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<User> getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserRowMapper());
            return Optional.ofNullable(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email = ?, first_name = ?, last_name = ?, birth_date = ?, address = ?, phone_number = ? WHERE id = ?";
        return jdbcTemplate.update(sql, user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getBirthDate(), user.getAddress(), user.getPhoneNumber(), user.getId()) > 0;
    }

    public boolean deleteUser(Long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, userId) > 0;
    }

    public Optional<List<User>> getUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate) {
        String sql = "SELECT * FROM users WHERE birth_date BETWEEN ? AND ?";
        try {
            List<User> users = jdbcTemplate.query(sql, new Object[]{fromDate, toDate}, new UserRowMapper());
            return Optional.of(users);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // RowMapper implementation
    private static final class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setBirthDate(rs.getDate("birth_date").toLocalDate());
            user.setAddress(rs.getString("address"));
            user.setPhoneNumber(rs.getString("phone_number"));
            return user;
        }
    }
}
