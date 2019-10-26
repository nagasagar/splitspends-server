package com.dasa.splitspends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dasa.splitspends.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("select u from User u where u.name = :name")
    List<User> findByName(@Param("name") String name);

    final String FIND_FRIENDS_SQL = "select * from users where " + "id in "
	    + "(select friend_id from friends where user_id = :searchID " + "union "
	    + "select user_id from friends where friend_id = :searchID);";

    @Query(value = FIND_FRIENDS_SQL, nativeQuery = true)
    List<User> findFriendsOfuser(@Param("searchID") Long id);

}
