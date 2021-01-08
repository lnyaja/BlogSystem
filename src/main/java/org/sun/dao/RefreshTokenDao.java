package org.sun.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.sun.pojo.RefreshToken;

public interface RefreshTokenDao extends JpaRepository<RefreshToken, String>, JpaSpecificationExecutor<RefreshToken> {

    RefreshToken findOneByTokenKey(String tokenKey);

    int deleteAllByUserId(String userId);

    int deleteAllByTokenKey(String tokenKey);
}
