package project5.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "token_expiration")
public class TokenExpirationEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id = 1;
    @Column(name = "token_expiration_time", nullable = true, unique = false, updatable = true)
    private long tokenExpirationTime = 24 * 60 * 60 * 1000;

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
