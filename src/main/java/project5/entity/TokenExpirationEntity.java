package project5.entity;

import jakarta.persistence.Id;

import java.io.Serializable;

public class TokenExpirationEntity implements Serializable {

    @Id
    private int id = 1;
    private long tokenExpirationTime;

    public TokenExpirationEntity() {
    }

    public TokenExpirationEntity(long tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public long getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(long tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }
}
