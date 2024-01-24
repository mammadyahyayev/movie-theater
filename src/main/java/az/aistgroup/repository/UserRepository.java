package az.aistgroup.repository;

import az.aistgroup.domain.dto.UserViewDto;
import az.aistgroup.domain.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findUserByUsername(String username);

    @Query("select distinct u from User u")
    List<UserViewDto> getAllUsers();
}
