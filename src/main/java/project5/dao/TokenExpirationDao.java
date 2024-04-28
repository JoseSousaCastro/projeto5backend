package project5.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.*;
import project5.entity.TokenExpirationEntity;

@Stateless
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
        em.persist(tokenExpirationEntity);
    }

    public void updateTokenExpirationTime(TokenExpirationEntity newExpirationTime) {
        TokenExpirationEntity tokenExpirationEntity = findTokenExpirationEntity(); // Obtém a entidade existente
        if (tokenExpirationEntity != null) {
            tokenExpirationEntity.setTokenExpirationTime(newExpirationTime.getTokenExpirationTime());
            em.merge(tokenExpirationEntity); // Atualiza a entidade no banco de dados
        }
    }

    public void initializeTokenExpirationEntity() {
        TokenExpirationEntity tokenExpirationEntity = new TokenExpirationEntity();
        em.persist(tokenExpirationEntity);
    }

}
