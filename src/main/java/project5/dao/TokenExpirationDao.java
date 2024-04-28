package project5.dao;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.*;
import project5.entity.TokenExpirationEntity;

public class TokenExpirationDao extends AbstractDao<TokenExpirationEntity>{

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(TokenExpirationDao.class);


    public TokenExpirationDao() {
        super(TokenExpirationEntity.class);
    }



    public TokenExpirationEntity findTokenExpirationEntity() {
        TypedQuery<TokenExpirationEntity> query = em.createQuery("SELECT t FROM TokenExpirationEntity t WHERE t.id = 1", TokenExpirationEntity.class);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void saveTokenExpirationTime(TokenExpirationEntity tokenExpirationEntity) {
        em.getTransaction().begin();
        em.persist(tokenExpirationEntity);
        em.getTransaction().commit();
    }

    public void updateTokenExpirationTime(TokenExpirationEntity tokenExpirationEntity) {
        em.getTransaction().begin();
        em.merge(tokenExpirationEntity);
        em.getTransaction().commit();
    }

}
