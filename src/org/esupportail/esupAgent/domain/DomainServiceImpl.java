/**
 * ESUP-Portail ESUP Agent - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-agent
 */
package org.esupportail.esupAgent.domain;

import org.esupportail.commons.services.application.ApplicationService;
import org.esupportail.commons.utils.BeanUtils;

import java.util.List;

import org.esupportail.esupAgent.dao.DaoService;
import org.esupportail.esupAgent.domain.beans.Agent;
import org.esupportail.esupAgent.domain.beans.User;
import org.esupportail.esupAgent.domain.beans.VersionManager;
import org.esupportail.commons.exceptions.ConfigException;
import org.esupportail.commons.exceptions.UserNotFoundException;
import org.esupportail.commons.services.application.Version;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.ldap.LdapUserService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.Assert;
import org.esupportail.commons.web.beans.Paginator;
import org.springframework.beans.factory.InitializingBean;
import org.esupportail.esupAgent.services.application.AgentApplicationServiceImpl;

;

/**
 * The basic implementation of DomainService.
 * 
 * See /properties/domain/domain-example.xml
 */
public class DomainServiceImpl implements DomainService, InitializingBean {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -8200845058340254019L;

	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	/**
	 * {@link LdapUserService}.
	 */
	private LdapUserService ldapUserService;

	/**
	 * The LDAP attribute that contains the display name.
	 */
	private String displayNameLdapAttribute;
	
	/**
	 * The LDAP attribute that contains the mail.
	 */
	private String mailLdapAttribute;

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	/**
	 * Bean constructor.
	 */
	public DomainServiceImpl() {
		super();
	}

	private ApplicationService getApplicationService() {
		return (ApplicationService) BeanUtils.getBean("applicationService");

	}

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.daoService, "property daoService of class "
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(this.ldapUserService,
				"property ldapUserService of class "
						+ this.getClass().getName() + " can not be null");
		Assert.hasText(this.displayNameLdapAttribute,
				"property displayNameLdapAttribute of class "
						+ this.getClass().getName() + " can not be null");
	}

	// ////////////////////////////////////////////////////////////
	// User
	// ////////////////////////////////////////////////////////////

	/**
	 * Set the information of a user from a ldapUser.
	 * 
	 * @param user
	 * @param ldapUser
	 * @return true if the user was updated.
	 */
	private boolean setUserInfo(final User user, final LdapUser ldapUser) {
		String displayName = null;
		Agent currentAgent = new Agent();
		List<String> displayNameLdapAttributes = ldapUser.getAttributes().get(
				displayNameLdapAttribute);
		if (displayNameLdapAttributes != null) {
			displayName = displayNameLdapAttributes.get(0);
		}
		if (displayName == null) {
			displayName = user.getId();
		}
		if (displayName.equals(user.getDisplayName())) {
			return false;
		}
		user.setDisplayName(displayName);

		List<User> lesAdmins = ((AgentApplicationServiceImpl) getApplicationService())
				.getConfigAgent().getAdmins();
		if (lesAdmins.contains(user)) {
			user.setAdmin(true);
		}

		List<String> mailLdapAttributes = ldapUser.getAttributes().get(mailLdapAttribute);
		if (mailLdapAttributes != null) {
			user.setMail(mailLdapAttributes.get(0));
		}
		

		String supannEmpId = ldapUser.getAttribute(((AgentApplicationServiceImpl) getApplicationService())
				.getConfigAgent().getLdap_HarpegeId());

		logger.debug("supannEmpId : " + supannEmpId);

		currentAgent.setSupannEmpId(new Integer(supannEmpId).intValue());
		currentAgent.setVisualisationCompte(((AgentApplicationServiceImpl) getApplicationService())
				.getConfigAgent().getVisualisationCompte());
		currentAgent.setTelephonePortableModifiable(((AgentApplicationServiceImpl) getApplicationService())
				.getConfigAgent().getTelephonePortableModifiable());
		currentAgent.setEmailModifiable(((AgentApplicationServiceImpl) getApplicationService())
				.getConfigAgent().getEmailModifiable());
		currentAgent.setAdresseModifiable(((AgentApplicationServiceImpl) getApplicationService())
				.getConfigAgent().getAdresseModifiable());
		currentAgent.setWsdl_anonymous(((AgentApplicationServiceImpl) getApplicationService())
				.getConfigAgent().getWsdl_anonymous());
		currentAgent.setWsdl_usr_name(((AgentApplicationServiceImpl) getApplicationService())
				.getConfigAgent().getWsdl_usr_name());
		currentAgent.setWsdl_usr_password(((AgentApplicationServiceImpl) getApplicationService())
				.getConfigAgent().getWsdl_usr_password());
		currentAgent.setWsdl_url_referentiel_geographique(((AgentApplicationServiceImpl) getApplicationService())
				.getConfigAgent().getWsdl_url_referentiel_geographique());
		
		currentAgent
				.setWsdl_url_dossier_rh_personnel(((AgentApplicationServiceImpl) getApplicationService())
						.getConfigAgent().getWsdl_url_dossier_rh_personnel());
		currentAgent.setWsdl_url_dossier_rh_administratif(((AgentApplicationServiceImpl) getApplicationService())
						.getConfigAgent().getWsdl_url_dossier_rh_administratif());
		try {
			if (currentAgent.getConsultationEtatCivil() == null) {
				logger.error("erreur consultation civil");

			} else if (currentAgent.getConsultationEtatCivil()
					.getIndividuReponseEtatCivil_V2() == null) {
				logger.error(currentAgent.getConsultationEtatCivil().toString());
				logger.error("erreur dans getNomPatronymique()");
			} else {
				logger.debug("nom : "
						+ currentAgent.getConsultationEtatCivil()
								.getIndividuReponseEtatCivil_V2()
								.getNomPatronymique());
				logger.debug("prenom : "
						+ currentAgent.getConsultationEtatCivil()
								.getIndividuReponseEtatCivil_V2().getPrenom());
				logger.debug("civilite : "
						+ currentAgent.getConsultationEtatCivil()
								.getIndividuReponseEtatCivil_V2().getCivilite());
			}
		} catch (Exception e) {
			logger.error("erreur" + e.getMessage());
		}

		try {
			user.setAgent(currentAgent);
		} catch (Exception e) {
			logger.error("stockage des informations de l'utilisateur impossible !");
		}
		return true;
	}

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#updateUserInfo(org.esupportail.esupAgent.domain.beans.User)
	 */
	public void updateUserInfo(final User user) {
		if (setUserInfo(user, ldapUserService.getLdapUser(user.getId()))) {
			updateUser(user);
		}
		List<User> lesAdmins = ((AgentApplicationServiceImpl) getApplicationService())
				.getConfigAgent().getAdmins();
		if (lesAdmins.contains(user)) {
			user.setAdmin(true);
		}
	}

	/**
	 * If the user is not found in the database, try to create it from a LDAP
	 * search.
	 * 
	 * @see org.esupportail.esupAgent.domain.DomainService#getUser(java.lang.String)
	 */
	public User getUser(final String id) throws UserNotFoundException {
		// User user = daoService.getUser(id);
		User user = null;
		if (user == null) {
			LdapUser ldapUser = this.ldapUserService.getLdapUser(id);
			user = new User();
			user.setId(ldapUser.getId());
			setUserInfo(user, ldapUser);
			daoService.addUser(user);
			logger.info("user '" + user.getId()
					+ "' has been added to the database");
		}
		return user;
	}

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#getUsers()
	 */
	public List<User> getUsers() {
		return this.daoService.getUsers();
	}

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#updateUser(org.esupportail.esupAgent.domain.beans.User)
	 */
	public void updateUser(final User user) {
		this.daoService.updateUser(user);
	}

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#addAdmin(org.esupportail.esupAgent.domain.beans.User)
	 */
	public void addAdmin(final User user) {
		user.setAdmin(true);
		updateUser(user);
	}

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#deleteAdmin(org.esupportail.esupAgent.domain.beans.User)
	 */
	public void deleteAdmin(final User user) {
		user.setAdmin(false);
		updateUser(user);
	}

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#getAdminPaginator()
	 */
	public Paginator<User> getAdminPaginator() {
		return this.daoService.getAdminPaginator();
	}

	/**
	 * @param displayNameLdapAttribute
	 *            the displayNameLdapAttribute to set
	 */
	public void setDisplayNameLdapAttribute(
			final String displayNameLdapAttribute) {
		this.displayNameLdapAttribute = displayNameLdapAttribute;
	}

	// ////////////////////////////////////////////////////////////
	// VersionManager
	// ////////////////////////////////////////////////////////////

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#getDatabaseVersion()
	 */
	public Version getDatabaseVersion() throws ConfigException {
		// VersionManager versionManager = daoService.getVersionManager();
		VersionManager versionManager = new VersionManager();
		versionManager.setVersion("0.0.1");
		if (versionManager == null) {
			return null;
		}
		return new Version(versionManager.getVersion());
	}

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#setDatabaseVersion(java.lang.String)
	 */
	public void setDatabaseVersion(final String version) {
		/*
		 * if (logger.isDebugEnabled()) {
		 * logger.debug("setting database version to '" + version + "'..."); }
		 * VersionManager versionManager = daoService.getVersionManager();
		 * versionManager.setVersion(version);
		 * daoService.updateVersionManager(versionManager); if
		 * (logger.isDebugEnabled()) { logger.debug("database version set to '"
		 * + version + "'."); }
		 */
	}

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#setDatabaseVersion(org.esupportail.commons.services.application.Version)
	 */
	public void setDatabaseVersion(final Version version) {
		setDatabaseVersion(version.toString());
	}

	// ////////////////////////////////////////////////////////////
	// Authorizations
	// ////////////////////////////////////////////////////////////

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#userCanViewAdmins(org.esupportail.esupAgent.domain.beans.User)
	 */
	public boolean userCanViewAdmins(final User user) {
		if (user == null) {
			return false;
		}
		return user.getAdmin();
	}

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#userCanAddAdmin(org.esupportail.esupAgent.domain.beans.User)
	 */
	public boolean userCanAddAdmin(final User user) {
		if (user == null) {
			return false;
		}
		return user.getAdmin();
	}

	/**
	 * @see org.esupportail.esupAgent.domain.DomainService#userCanDeleteAdmin(org.esupportail.esupAgent.domain.beans.User,
	 *      org.esupportail.esupAgent.domain.beans.User)
	 */
	public boolean userCanDeleteAdmin(final User user, final User admin) {
		if (user == null) {
			return false;
		}
		if (!user.getAdmin()) {
			return false;
		}
		return !user.equals(admin);
	}

	// ////////////////////////////////////////////////////////////
	// Misc
	// ////////////////////////////////////////////////////////////

	/**
	 * @param daoService
	 *            the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	/**
	 * @param ldapUserService
	 *            the ldapUserService to set
	 */
	public void setLdapUserService(final LdapUserService ldapUserService) {
		this.ldapUserService = ldapUserService;
	}

	public String getMailLdapAttribute() {
		return mailLdapAttribute;
	}

	public void setMailLdapAttribute(String mailLdapAttribute) {
		this.mailLdapAttribute = mailLdapAttribute;
	}

}
