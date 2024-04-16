package project5.dao;

import jakarta.persistence.Query;
import project5.dto.UserRegistrationInfo;
import project5.entity.UserEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.time.LocalDate;
import java.util.ArrayList;

@Stateless
public class UserDao extends AbstractDao<UserEntity> {

	private static final long serialVersionUID = 1L;

	public UserDao() {
		super(UserEntity.class);
	}


	public UserEntity findUserByToken(String token) {
		try {
			return (UserEntity) em.createNamedQuery("User.findUserByToken").setParameter("token", token)
					.getSingleResult();

		} catch (NoResultException e) {
			return null;
		}
	}

	public UserEntity findUserByUsername(String username) {
		try {
			return (UserEntity) em.createNamedQuery("User.findUserByUsername").setParameter("username", username)
					.getSingleResult();

		} catch (NoResultException e) {
			return null;
		}
	}


	public UserEntity findUserByEmail(String email) {
		try {
			return (UserEntity) em.createNamedQuery("User.findUserByEmail").setParameter("email", email)
					.getSingleResult();

		} catch (NoResultException e) {
			return null;
		}
	}

	public UserEntity findUserByPhone(String phone) {
		try {
			return (UserEntity) em.createNamedQuery("User.findUserByPhone").setParameter("phone", phone)
					.getSingleResult();

		} catch (NoResultException e) {
			return null;
		}
	}

	public UserEntity findUserByUsernameAndPassword(String username, String password){
		try{
			return (UserEntity) em.createNamedQuery("User.findUserByUsernameAndPassword")
					.setParameter("username", username)
					.setParameter("password", password)
					.getSingleResult();
		}catch (NoResultException e){
			return null; //Nenhum user foi encontrado com estes dados
		}
	}

	public ArrayList<UserEntity> findAllUsers() {
		try {
			return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllUsers").getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public ArrayList<UserEntity> findAllUsersByTypeOfUser(int typeOfUser) {
		try {
			return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllUsersByTypeOfUser").setParameter("typeOfUser", typeOfUser).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public ArrayList<UserEntity> findAllUsersByVisibility(boolean visible) {
		try {
			return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllUsersByVisibility").setParameter("visible", visible).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public ArrayList<UserEntity> findAllUsersByTypeOfUserAndVisibility(int typeOfUser, boolean visible) {
		try {
			return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllUsersByTypeOfUserByVisibility").setParameter("typeOfUser", typeOfUser)
					.setParameter("visible", visible).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public ArrayList<UserEntity> findAllUsersByIsConfirmed(boolean confirmed) {
		try {
			return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllUsersByIsConfirmed").setParameter("confirmed", confirmed).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public ArrayList<UserEntity> findAllConfirmedAndNotErasedUsers() {
		try {
			return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllConfirmedAndNotErasedUsers").getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public ArrayList<UserRegistrationInfo> getUsersRegisteredOverTime() {
		ArrayList<UserRegistrationInfo> registrationInfoList = new ArrayList<>();

		// Obtenha a data mínima e máxima dos registros de usuários confirmados
		LocalDate minDate = (LocalDate) em.createQuery("SELECT MIN(u.creationDate) FROM UserEntity u WHERE u.confirmed = true").getSingleResult();
		LocalDate maxDate = (LocalDate) em.createQuery("SELECT MAX(u.creationDate) FROM UserEntity u WHERE u.confirmed = true").getSingleResult();

		// Inicialize o total acumulado
		long accumulatedTotal = 0;

		// Loop através de cada dia entre minDate e maxDate
		for (LocalDate date = minDate; date.isBefore(maxDate.plusDays(1)); date = date.plusDays(1)) {
			// Consulta para obter o número de usuários registrados nesse dia
			Query query = em.createNamedQuery("User.findUsersRegisteredOnDate");
			query.setParameter("creationDate", date);
			Long count = (Long ) query.getSingleResult();

			// Converta o resultado para int
			int countInt = count != null ? count.intValue() : 0;

			// Adicione o total acumulado para este dia
			accumulatedTotal += countInt;

			// Adicione a informação do registro para este dia à lista
			registrationInfoList.add(new UserRegistrationInfo(date, (int) accumulatedTotal));
		}

		return registrationInfoList;
	}



}
