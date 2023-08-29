package com.mongodb_example.main.model.repositories;

import com.mongodb_example.main.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SteamUserRepository extends MongoRepository<User, String> {
    

    @Query("{username:'?0'}")
    User findUserByUsername(String username);

}
